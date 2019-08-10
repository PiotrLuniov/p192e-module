node {
  try {
    def student = 'pluniov'
    stage('Preparation') {
     checkout scm
    }

    stage('Health page'){
			sh 'sed -i "s/_buildnumber_/${BUILD_NUMBER}/g" config/main.html'
			sh 'sed -i "s/_builddate_/$(date)/g" config/main.html'
			sh 'cp config/main.html helloworld-ws/src/main/webapp/main.html'
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
      sh 'sed -i "s/_buildnumber_/${BUILD_NUMBER}/g" config/tomcat-k8s-deployment.yml'
      sh '$HOME/kubectl apply -f config/tomcat-k8s-deployment.yml'
    }

    stage('Health check'){
      sh '''
      sleep 10
      CURL_HEALTH=$(curl -IL http://pluniov-app.k8s.playpit.by/main.html)
      if [ $(echo "$CURL_HEALTH" | grep -c 'HTTP/1.1 200') -eq 1 ]
      then
        echo "Helthcheck is passed"
      fi
        '''
    }

    currentBuild.result = 'SUCCESS'
    echo "BUILD_SUCCESS"
    //mail bcc: '', body: 'BUILD_SUCCESS<br>Project:${JOB_NAME}<br>BUILD_NUMBER:${BUILD_NUMBER}' cc: '', from: '', replyTo: '', subject: 'Successful deployment', to: 'pluniov@gmail.com'

  }

  catch (err) {
    currentBuild.result = 'FAILURE'
    echo "BUILD_FAILURE"
    //mail bcc: '', body: 'BUILD_FAILURE<br>Project:${JOB_NAME}<br>BUILD_NUMBER:${BUILD_NUMBER}<br>Errors:${err}' cc: '', from: '', replyTo: '', subject: 'Failed deployment', to: 'pluniov@gmail.com'
  }

}
