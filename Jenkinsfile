node {
    def student = 'pramanouski'
    stage('1-Jenkisfile') {
     checkout scm
    }

    stage('1-Build') {
      withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', maven: 'Maven 3.6.1') {
        sh 'mvn clean package -f helloworld-ws/pom.xml'
      }
    }

    stage('2-Sonar') {
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

    stage('Tests') {
     parallel(
        'pre-integration-test': {
              withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', maven: 'Maven 3.6.1'){
                sh 'mvn pre-integration-test -f helloworld-ws/pom.xml'
              }
        },
        'integration-test': {
              withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', maven: 'Maven 3.6.1'){
                sh 'mvn integration-test -f helloworld-ws/pom.xml'
              }
        },
        'post-integration-test': {
              withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', maven: 'Maven 3.6.1'){
                sh 'mvn post-integration-test -f helloworld-ws/pom.xml'
              }
        }
      )
    }
