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
        withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
             sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.1.1398:sonar'
        }
    }
}