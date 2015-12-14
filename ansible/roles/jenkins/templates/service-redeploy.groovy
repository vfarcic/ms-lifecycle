import groovy.json.JsonSlurper

def swarmMaster = "10.100.192.200"
def proxy = "10.100.192.200"
def currentColor = getCurrentColor(swarmMaster, service)
def projectName = service.replaceAll("-", "")

node("cd") {
    env.PYTHONUNBUFFERED = 1

    stage "> Provisioning"
    try {
        sh "ansible-playbook /vagrant/ansible/{{ swarm_playbook }} \
            -i /vagrant/ansible/hosts/prod"
        sh "ansible-playbook /vagrant/ansible/nginx.yml \
            -i /vagrant/ansible/hosts/prod --extra-vars \
            \"proxy_host=swarm-master\""
    } catch(e) {}

    stage "> Re-Deployment"
    git url: "https://github.com/vfarcic/${service}.git"
    env.DOCKER_HOST = "tcp://${swarmMaster}:2375"
    def instances = getInstances(swarmMaster, service)
    sh "docker-compose -f docker-compose-swarm.yml \
        -p ${projectName} \
        --x-networking up -d db"
    sh "docker-compose -f docker-compose-swarm.yml \
        -p ${projectName} \
        --x-networking scale app-${currentColor}=$instances"
    updateProxy(swarmMaster, service, currentColor);
}

def getCurrentColor(swarmMaster, service) {
    try {
        return "http://${swarmMaster}:8500/v1/kv/${service}/color?raw".toURL().text
    } catch(e) {
        return ""
    }
}

def getInstances(swarmMaster, service) {
    return "http://${swarmMaster}:8500/v1/kv/${service}/instances?raw".toURL().text
}

def updateProxy(swarmMaster, service, color) {
    sh "consul-template -consul ${swarmMaster}:8500 \
        -template 'nginx-upstreams-${color}.ctmpl:nginx-upstreams.conf' -once"
    stash includes: 'nginx-*.conf', name: 'nginx'
    node("lb") {
        unstash 'nginx'
        sh "sudo cp nginx-includes.conf /data/nginx/includes/${service}.conf"
        sh "sudo cp nginx-upstreams.conf /data/nginx/upstreams/${service}.conf"
        env.DOCKER_HOST = ""
        sh "docker kill -s HUP nginx"
    }
}
