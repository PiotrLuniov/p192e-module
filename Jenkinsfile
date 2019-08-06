node {
   stage('Preparation') {
      git branch: 'akuznetsova', url: 'https://github.com/MNT-Lab/p192e-module.git'

   }
   stage('Build') {
     sh 'ls ../../.m2'
      withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
          sh 'mvn -f helloworld-ws/pom.xml package'
      }
   }
   stage('Scan') {
     def scannerHome = tool 'SonarQubeScanner';
     withSonarQubeEnv() {
         sh "${scannerHome}/bin/sonar-scanner " +
         '-Dsonar.projectKey=helloworld-ws:akuznetsova ' +
         '-Dsonar.language=java ' +
         '-Dsonar.sources=helloworld-ws/ '+
         '-Dsonar.java.binaries=**/target/classes'
      }
   }
   stage('Tests') {
    parallel(
        'Pre Integration': {
              withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                sh 'cd helloworld-ws/ &&  ' +
                'mvn pre-integration-test'
              }
        },
        'Integration': {
              withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                sh 'cd helloworld-ws/ &&  ' +
                'mvn integration-test'
              }
        },
        'Post Integration': {
              withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                sh 'cd helloworld-ws/ &&  ' +
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
      sh 'tar -czf pipeline-akuznetsova-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war'
      archiveArtifacts 'pipeline-akuznetsova-${BUILD_NUMBER}.tar.gz'
    },
    'Create Dockerfile': {
      sh 'echo "Placeholder for Dockerfile"'
    }
    )
}
}
