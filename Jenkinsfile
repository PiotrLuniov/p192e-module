node {
    stage('preparation') {
        git branch: 'hbledai', url: 'https://github.com/MNT-Lab/p192e-module.git'
    }
    stage('Building code'){
        withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
            sh 'mvn clean package -f helloworld-ws/pom.xml '
        }
    }
    stage(){
        def scannerHome = tool 'SonarQubeScanner';
    withSonarQubeEnv() { // If you have configured more than one global server connection, you can specify its name
      sh "${scannerHome}/bin/sonar-scanner" +
          '-Dsonar.projectKey=hbledai:project' +
          '-Dsonar:projectName=hbledai project' +
          '-Dsonar.sources=helloworld-project/' +
          '-Dsonar.java.binaries=**/target/classes' +
          '-Dsonar.language=java'
        }
    }
}