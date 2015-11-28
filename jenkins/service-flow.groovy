import groovy.json.JsonSlurper

node("cd") {
    def service = "books-ms"
    def registry = "10.100.198.200:5000/"
    def swarmMaster = "10.100.192.200"
    def proxy = "10.100.192.200"
    def currentColor = getCurrentColor(swarmMaster, service)
    def nextColor = getNextColor(service, currentColor)
    env.PYTHONUNBUFFERED = 1

    stage "> Provisioning"
    if (provision == true) {
        sh "ansible-playbook /vagrant/ansible/swarm.yml -i /vagrant/ansible/hosts/prod"
    }

    stage "> Pre-Deployment"
    git url: "https://github.com/vfarcic/${service}.git"
    if (build == "true") {
        sh "sudo docker build -t ${registry}${service}-tests -f Dockerfile.test ."
        sh "sudo docker-compose -f docker-compose-dev.yml run --rm tests"
        def app = docker.build "${registry}${service}"
        app.push()
    }

    stage "> Deployment"
    env.DOCKER_HOST = "tcp://${swarmMaster}:2375"
    def instances = getInstances(swarmMaster, service)
    if (build == "true") {
        sh "docker-compose -f docker-compose-swarm.yml pull app-${nextColor}"
    }
    sh "docker-compose -f docker-compose-swarm.yml --x-networking up -d db"
    sh "docker-compose -f docker-compose-swarm.yml rm -f app-${nextColor}"
    sh "docker-compose -f docker-compose-swarm.yml --x-networking scale app-${nextColor}=$instances"
    sh "curl -X PUT -d $instances http://${swarmMaster}:8500/v1/kv/${service}/instances"

    stage "> Post-Deployment"
    def address = getAddress(swarmMaster, service, nextColor)
    env.DOCKER_HOST = ""
    try {
        sh "docker-compose -f docker-compose-dev.yml run --rm -e DOMAIN=http://1$address integ"
    } catch (e) {
        sh "docker-compose -f docker-compose-swarm.yml stop app-${nextColor}"
        error("Pre-integration tests failed")
    }
    updateProxy(swarmMaster, service, nextColor)
    try {
        sh "docker-compose -f docker-compose-dev.yml run --rm -e DOMAIN=http://${proxy} integ"
    } catch (e) {
        if (currentColor != "") {
            updateProxy(swarmMaster, service, currentColor)
        }
        sh "docker-compose -f docker-compose-swarm.yml stop app-${nextColor}"
        error("Post-integration tests failed")
    }
    sh "curl -X PUT -d ${nextColor} http://${swarmMaster}:8500/v1/kv/${service}/color"
    if (currentColor != "") {
        env.DOCKER_HOST = "tcp://${swarmMaster}:2375"
        sh "docker-compose -f docker-compose-swarm.yml stop app-${currentColor}"
    }
    // TODO: Add pings
    env.DOCKER_HOST = ""
    sh "docker push ${registry}${service}-tests"
}

def getCurrentColor(swarmMaster, service) {
    try {
        return "http://${swarmMaster}:8500/v1/kv/${service}/color?raw".toURL().text
    } catch(e) {
        return ""
    }
}

def getNextColor(service, currentColor) {
    if (currentColor == "blue") {
        return "green"
    } else {
        return "blue"
    }
}

def getInstances(swarmMaster, service) {
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

def getAddress(swarmMaster, service, color) {
    echo "http://${swarmMaster}:8500/v1/catalog/service/${service}-${color}"
    def serviceJson = "http://${swarmMaster}:8500/v1/catalog/service/${service}-${color}".toURL().text
    def result = new JsonSlurper().parseText(serviceJson)[0]
    return result.ServiceAddress + ":" + result.ServicePort
}

def updateProxy(swarmMaster, service, color) {
    def dir = pwd()
    sh "consul-template -consul ${swarmMaster}:8500 -template 'nginx-upstreams-${color}.ctmpl:nginx-upstreams.conf' -once"
    sh "ansible-playbook /vagrant/ansible/nginx-update.yml -i /vagrant/ansible/hosts/prod --extra-vars 'repo_dir=${dir} service_name=${service}'"
}
