import groovy.json.JsonSlurper

def service = "books-ms"
def registry = "10.100.198.200"
def swarmMaster = "10.100.192.200"
def proxy = "10.100.192.200"

// TODO: Test normal both colors
// TODO: Test pre-deployment failure
// TODO: Test pre-integration failure
// TODO: Test post-integration failure
// TODO: Add to Ansible

node("cd") {
    def dir = pwd()
    def nextColor = getNextColor()
    def currentColor = getCurrentColor()
    env.PYTHONUNBUFFERED = 1

    stage "> Provisioning"
    if (provision) {
        sh "ansible-playbook /vagrant/ansible/swarm.yml -i /vagrant/ansible/hosts/prod"
    }

    stage "> Pre-Deployment"
    git url: "https://github.com/vfarcic/${service}.git"
    if (build) {
        sh "sudo docker build -t ${registry}:5000/${service}-tests -f Dockerfile.test ."
    }
    sh "sudo docker-compose -f docker-compose-dev.yml run --rm tests"
    if (build) {
        def app = docker.build "${registry}:5000/${service}"
    }
    if (push) {
        app.push()
    }

    stage "> Deployment"
    env.DOCKER_HOST = "tcp://${swarmMaster}:2375"
    def instances = getInstances()
    if (pull) {
        sh "docker-compose -f docker-compose-swarm.yml pull app-${nextColor}"
    }
    sh "docker-compose -f docker-compose-swarm.yml --x-networking up -d db"
    sh "docker-compose -f docker-compose-swarm.yml rm -f app-${nextColor}"
    sh "docker-compose -f docker-compose-swarm.yml --x-networking scale app-${nextColor}=$instances"
    sh "curl -X PUT -d $instances http://${swarmMaster}:8500/v1/kv/${service}/instances"

    stage "> Post-Deployment"
    def address = getAddress()
    env.DOCKER_HOST = ""
    try {
        sh "docker-compose -f docker-compose-dev.yml run --rm -e DOMAIN=http://$address integ"
    } catch (e) {
        error("Pre-integration tests failed")
    }
    updateProxy(nextColor)
    try {
        sh "docker-compose -f docker-compose-dev.yml run --rm -e DOMAIN=http://${proxy} integ"
    } catch (e) {
        updateProxy(currentColor)
        sh "docker-compose -f docker-compose-swarm.yml stop app-${nextColor}"
        error("Post-integration tests failed")
    }
    sh "curl -X PUT -d ${nextColor} http://${swarmMaster}:8500/v1/kv/${service}/color"
    env.DOCKER_HOST = "tcp://${swarmMaster}:2375"
    sh "docker-compose -f docker-compose-swarm.yml stop app-${currentColor}"
    // TODO: Add pings
    env.DOCKER_HOST = ""
    sh "docker push ${registry}:5000/${service}-tests"
}

def getCurrentColor() {
    try {
        return "http://${swarmMaster}:8500/v1/kv/${service}/color?raw".toURL().text
    } catch(e) {
        return ""
    }
}

def getNextColor() {
    def color = getCurrentColor()
    if (color == "blue") {
        return "green"
    } else {
        return "blue"
    }
}

def getInstances() {
    instances = instances.toInteger()
    if (instances == 0) {
        try {
            instances = "http://${swarmMaster}:8500/v1/kv/${service}/instances?raw".toURL().text
        } catch (e) {
            return 1
        }
    }
    return instances
}

def getAddress() {
    def service = "http://${swarmMaster}:8500/v1/catalog/service/${service}-${nextColor}".toURL().text
    def slurper = new JsonSlurper()
    def result = slurper.parseText(service)[0]
    return result.ServiceAddress + ":" + result.ServicePort
}

def updateProxy(color) {
    sh "consul-template -consul ${swarmMaster}:8500 -template 'nginx-upstreams-${color}.ctmpl:nginx-upstreams.conf' -once"
    sh "ansible-playbook /vagrant/ansible/nginx-update.yml -i /vagrant/ansible/hosts/prod --extra-vars 'repo_dir=${dir} service_name=${service}'"
}
