node {
    def student = 'pluniov'
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
         '-Dsonar.projectKey=helloworld-ws-pluniov '+
         '-Dsonar.language=java '+
         '-Dsonar.sources=helloworld-ws/src '+
         '-Dsonar.java.binaries=helloworld-ws/target'
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

    stage('Triggering job and fetching artefact after finishing') {
      build job: "MNTLAB-${student}-child1-build-job", parameters: [[$class: 'StringParameterValue', name: 'BRANCH_NAME', value: "${student}"]], wait: true
      copyArtifacts filter: "${student}_dsl_script.tar.gz", fingerprintArtifacts: true, projectName: "MNTLAB-${student}-child1-build-job", selector: lastSuccessful()
      sh "tar -xvzf ${student}_dsl_script.tar.gz"
    }

}
