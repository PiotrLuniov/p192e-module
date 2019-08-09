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

    stage('Packaging and Publishing results'){
      parallel(
        'Archiving artifact': {
          sh "tar cvzf pipeline-${student}-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war"
          nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: "pipeline-${student}-\${BUILD_NUMBER}.tar.gz"]], mavenCoordinate: [artifactId: "${student}", groupId: 'pipeline', packaging: 'tar.gz', version: '${BUILD_NUMBER}']]]
        },
        'Creating Docker Image': {
          withDockerRegistry(credentialsId: 'nexus', toolName: 'dockerTool', url: 'http://localhost:6566') {
            sh "docker build -t localhost:6566/helloworld-${student}:${BUILD_NUMBER} -f config/Dockerfile ."
            sh "docker push localhost:6566/helloworld-${student}:${BUILD_NUMBER}"
          }
        }
      )
    }

    stage('Asking for manual approval'){
      timeout(time: 1, unit: 'MINUTES') {
             input(id: "Deployment of artifact", message: "Deploy to Kubernetes?", ok: "Deploy")
      }
    }

    stage('Deployment') {
      sh '$HOME/kubectl apply -f config/k8s-deploy.yml'
    }

}
