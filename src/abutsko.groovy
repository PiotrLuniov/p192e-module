def pushDockerImage(String nameImage, String nameRepo, String credentialId, String Dockerfile = 'Dockerfile') {
    docker.withRegistry("https://${nameRepo}", "${credentialId}") {
        def appImage = docker.build("${nameRepo}/${nameImage}", "-f ${Dockerfile} .")
        appImage.push()
    }
}
