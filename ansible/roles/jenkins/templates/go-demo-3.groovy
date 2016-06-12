import groovy.json.JsonSlurper
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod

def proxy = "10.100.198.200"
def consul = "http://10.100.192.200:8500"
def envs = [
        "FLOW_PROXY_HOST=${proxy}",
        "FLOW_PROXY_RECONF_PORT=8081",
        "FLOW_CONSUL_ADDRESS=${consul}",
        "FLOW_PROXY_DOCKER_HOST=tcp://${proxy}:2375",
        "DOCKER_HOST=tcp://swarm-master:2375"
]

node("cd") {
    git "https://github.com/vfarcic/go-demo.git"

    stage "pre-dep-tests"
    sh "docker-compose -f docker-compose-test.yml run --rm unit"

    stage "build"
    sh "docker build -t vfarcic/go-demo ."
    // sh "docker push vfarcic/go-demo"

    // This is new!!!
    stage "deploy"
    withEnv(envs) {
        sh "docker-flow -p go-demo --flow=deploy --scale=\"${SCALE}\""
    }

    // This is new!!!
    stage "pre-integ-tests"
    def addresses = getAddresses(consul)
    def tests = [:]
    for (address in addresses) {
        def host = "${address.ServiceAddress}:${address.ServicePort}"
        def index = "${address.CreateIndex}"
        tests[index] = { sh "HOST_IP=${host} docker-compose -p go-demo-tests-${index} -f docker-compose-test.yml run --rm production" }
    }
    parallel tests

    // This is new!!!
    stage "integrate"
    withEnv(envs) {
        sh "docker-flow -p go-demo --flow=proxy --flow=stop-old"
    }

    // This is new!!!
    stage "post-integ-tests"
    sh "HOST_IP=${proxy} docker-compose -f docker-compose-test.yml run --rm production"
}

def sendHttpRequest(url) {
    def get = new GetMethod(url)
    new HttpClient().executeMethod(get)
    def response = get.getResponseBody()
    get.releaseConnection()
    return new String(response)
}

def getAddresses(consul) {
    def color = sendHttpRequest("${consul}/v1/kv/docker-flow/go-demo-app/color?raw")
    def response = sendHttpRequest("${consul}/v1/catalog/service/go-demo-app-${color}")
    return new JsonSlurper().parseText(response)
}
