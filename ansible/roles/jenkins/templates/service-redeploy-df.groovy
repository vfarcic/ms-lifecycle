node("cd") {
    git url: "https://github.com/vfarcic/${serviceName}.git"
    dockerFlow(serviceName, ["deploy", "proxy", "stop-old"])
}