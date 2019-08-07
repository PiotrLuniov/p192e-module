node {
    def student = 'pramanouski'
    stage('Preparation') {
     checkout scm
    }

    stage('Building code') {
      withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', maven: 'Maven 3.6.1') {
        sh 'mvn clean package -f helloworld-ws/pom.xml'
      }
    }

    stage('Sonar scan') {
      def scannerHome = tool 'SonarQubeScanner'
      withSonarQubeEnv('sonar-ci') {
         sh "${scannerHome}/bin/sonar-scanner " +
         '-Dsonar.projectKey=helloworld-ws-pramanouski '+
         '-Dsonar.language=java '+
         '-Dsonar.sources=helloworld-ws/src '+
         '-Dsonar.java.binaries=helloworld-ws/target'
       }
    }
  }
