node("cd") {
    git "https://github.com/vfarcic/go-demo.git"

    stage "pre-dep-tests"
    sh "docker-compose -f docker-compose-test.yml run --rm unit"

    stage "build"
    sh "docker build -t vfarcic/go-demo ."
    // sh "docker push vfarcic/go-demo"

    stage "deploy"
    withEnv(['DOCKER_HOST=tcp://swarm-master:2375']) {
        sh "docker-compose up -d"
        sh "docker-compose ps"
    }
}