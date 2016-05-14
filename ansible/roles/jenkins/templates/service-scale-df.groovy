node("cd") {
    git url: "https://github.com/vfarcic/${serviceName}.git"
    dockerFlow(serviceName, ["scale", "proxy"], ["--scale=\"" + scale + "\""])
}