@Library('akuznetsova-shared-library') _
node {
  def result=''
  try{
  def studentName = 'akuznetsova'
   stage('Preparation') {
      git branch: 'akuznetsova', url: 'https://github.com/MNT-Lab/build-t00ls.git'

   }
   stage('Build') {
      withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
          sh 'mvn -f helloworld-project/helloworld-ws/pom.xml package'
      }
   }
   stage('Scan') {
     def scannerHome = tool 'SonarQubeScanner';
     withSonarQubeEnv() {
         sh "${scannerHome}/bin/sonar-scanner " +
         '-Dsonar.projectKey=helloworld-ws:akuznetsova ' +
         '-Dsonar.language=java ' +
         '-Dsonar.sources=helloworld-project/ '+
         '-Dsonar.java.binaries=**/target/classes'
      }
   }
   stage('Tests') {
    parallel(
        'Pre Integration': {
              withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                sh 'cd helloworld-project/helloworld-ws/ &&  ' +
                'mvn pre-integration-test'
              }
        },
        'Integration': {
              withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                sh 'cd helloworld-project/helloworld-ws/ &&  ' +
                'mvn integration-test'
              }
        },
        'Post Integration': {
              withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                sh 'cd helloworld-project/helloworld-ws/ &&  ' +
                'mvn post-integration-test'
              }
        }
    )
}
stage('Build child'){
     sh 'cd ${WORKSPACE}'
     build job: 'MNTLAB-akuznetsova-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'akuznetsova')], wait: true
     copyArtifacts filter: 'output.txt', projectName: 'MNTLAB-akuznetsova-child1-build-job'
}
stage('Archieve and Dockerfile'){
  parallel(
    'Create archieve': {
      sh 'tar -czf pipeline-akuznetsova-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-project/helloworld-ws/target/helloworld-ws.war'
      archiveArtifacts 'pipeline-akuznetsova-${BUILD_NUMBER}.tar.gz'
      nexus_push('MNT-pipeline-training', 'akuznetsova')
    },
    'Create Dockerfile': {
      sh '''
cat << EOF > $WORKSPACE/Dockerfile
From tomcat:8-jre8
ADD helloworld-project/helloworld-ws/target/helloworld-ws.war  /usr/local/tomcat/webapps/
EOF
'''
      nexus_push('docker', 'akuznetsova')
    }
    )
}
stage('Ask for approval'){
  timeout(time: 10, unit: 'MINUTES') {
				input(id: "Try to deploy?", message: "Deploy helloworld-akuznetsova:${BUILD_NUMBER}?", ok: "deploy!")
				}
}
stage('Deployment'){
    sh "wget https://raw.githubusercontent.com/MNT-Lab/build-t00ls/akuznetsova/tomcat_app.yml -O tomcat_app.yml"
    sh 'sed -i "s/_buildNumber_/${BUILD_NUMBER}/g" tomcat_app.yml'
    sh "$HOME/kubectl apply --namespace=akuznetsova -f tomcat_app.yml"
}
  result = 'success';
}

	catch (err) {
        def now = new Date()
        def body = "There are errors in pipeline:\n${err}\nBuild: ${BUILD_NUMBER}\nErrors has appeared: ${now}"
        println body
		emailext body: "${body}", subject: 'Pipeline errors!', to: 'alexminsk.noir@gmail.com'
		result = 'fail'
	}

	finally {
		if(result == 'success'){
			echo "Pipeline has successfully done."

			def now = new Date()
			def body = "Pipeline has successfully done at: ${now}\nBUild number: ${BUILD_NUMBER}"
			emailext body: "${body}", subject: 'Pipeline success!', to: 'alexminsk.noir@gmail.com'
		}
	}
}
