@Library('akuznetsova-shared-library') _
node {
  def studentName = 'akuznetsova'
   stage('Preparation') {
      git branch: 'akuznetsova', url: 'https://github.com/MNT-Lab/build-t00ls.git'

   }
   stage('Build') {
     sh 'ls ../../EPBYMINW9149/'
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
      nexus_push('MNT-pipeline-training', studentName)
    },
    'Create Dockerfile': {
      sh '''
cat << EOF > $WORKSPACE/Dockerfile
From tomcat:8-jre8
ADD helloworld-ws/target/helloworld-ws.war  /usr/local/tomcat/webapps/
EOF
'''
      nexus_push('docker', studentName)
    }
    )
}
stage('Ask for approval'){
  timeout(time: 10, unit: 'MINUTES') {
				input(id: "Try to deploy", message: "Deploy helloworld-${studentName}:${env.BUILD_NUMBER}?", ok: 'Deploying')
				}
}
}
