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
        withSonarQubeEnv(jdk: 'JDK9', credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac', installationName: 'sonar-ci') {
          '-Dsonar.projectKey=hbledai:project' +
          '-Dsonar:projectName=hbledai project' +
          '-Dsonar.sources=helloworld-ws/src/main/java' +
          '-Dsonar.java.binaries=**/target/classes' +
          '-Dsonar.language=java'
        }
    }
}