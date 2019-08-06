node {
   def mvnHome
   stage('Preparation') {
      git branch: 'akuznetsova', url: 'https://github.com/MNT-Lab/build-t00ls.git'

   }
   stage('Build') {
       sh 'cd helloworld-project/helloworld-ws/'
      withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
          sh 'mvn -f helloworld-project/helloworld-ws/pom.xml test package'
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
}
