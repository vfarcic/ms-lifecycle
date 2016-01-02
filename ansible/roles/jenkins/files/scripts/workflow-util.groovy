def provision(playbook) {
    stage "Provision"
    env.PYTHONUNBUFFERED = 1
    sh "ansible-playbook /vagrant/ansible/${playbook} \
        -i /vagrant/ansible/hosts/prod"
}

def buildTests(serviceName, registryIpPort) {
    stage "Build tests"
    def tests = docker.image("${registryIpPort}/${serviceName}-tests")
    try {
        tests.pull()
    } catch(e) {}
    sh "docker build -t \"${registryIpPort}/${serviceName}-tests\" \
        -f Dockerfile.test ."
    tests.push()
}

def runTests(serviceName, target, extraArgs) {
    stage "Run ${target} tests"
    sh "docker-compose -f docker-compose-dev.yml \
        -p ${serviceName} run --rm ${extraArgs} ${target}"
}

def buildService(serviceName, registryIpPort) {
    stage "Build service"
    def service = docker.image("${registryIpPort}/${serviceName}")
    try {
        service.pull()
    } catch(e) {}
    docker.build "${registryIpPort}/${serviceName}"
    service.push()
}

def deploy(serviceName, prodIp) {
    stage "Deploy"
    withEnv(["DOCKER_HOST=tcp://${prodIp}:2375"]) {
        try {
            sh "docker-compose pull app"
        } catch(e) {}
        sh "docker-compose -p ${serviceName} up -d app"
    }
}

def updateProxy(serviceName, proxyNode) {
    stage "Update proxy"
    stash includes: 'nginx-*', name: 'nginx'
    node(proxyNode) {
        unstash 'nginx'
        sh "sudo cp nginx-includes.conf /data/nginx/includes/${serviceName}.conf"
        sh "sudo consul-template \
            -consul localhost:8500 \
            -template \"nginx-upstreams.ctmpl:/data/nginx/upstreams/${serviceName}.conf:docker kill -s HUP nginx\" \
            -once"
    }
}

return this;
