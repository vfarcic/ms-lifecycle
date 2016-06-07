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
        sh "docker-flow -p go-demo --flow=deploy --flow=proxy --flow=stop-old"
    }
}