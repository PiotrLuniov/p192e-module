node {
   def mvnHome
   stage('Preparation') {
      git branch: 'akuznetsova', url: 'https://github.com/MNT-Lab/build-t00ls.git'

   }
   stage('Build') {
       sh 'cd helloworld-project/helloworld-ws/'
      withMaven(jdk: 'java-8', maven: 'Maven 3.6.1'){
          sh 'mvn -f helloworld-project/helloworld-ws/pom.xml test package'
      }
   }
   stage('Scan') {
     def scannerHome = tool 'SonarQubeScanner';
     withSonarQubeEnv() {
         sh "${scannerHome}/bin/sonar-scanner"
      }
   }
}
