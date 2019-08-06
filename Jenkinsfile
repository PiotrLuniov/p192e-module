node {
    stage('preparation') {
        git branch: 'hbledai', url: 'https://github.com/MNT-Lab/p192e-module.git'
    }
    stage('Building code'){
        withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
            sh 'mvn -f helloworld-ws/pom.xml package'
        }
    }
    stage(){
        withSonarQubeEnv(installationName: 'sonar-ci') {
          '-Dsonar.projectKey=hbledai:project' +
          '-Dsonar:projectName=hbledai project' +
          '-Dsonar.sources=helloworld-ws/src/main/java' +
          '-Dsonar.java.binaries=**/target/classes' +
          '-Dsonar.language=java'
        }
    }
}