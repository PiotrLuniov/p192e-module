node('Host-Node'){
	def
   stage('Preparation') {
      git branch: 'kshevchenko', url: 'https://github.com/MNT-Lab/p192e-module.git'
}
   sh "ls -laR"
  stage('Building code'){
			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
				sh 'mvn clean package -f helloworld-ws/pom.xml'
			}
}
	stage('Sonar scan') {
		def sqScannerHome = tool 'SonarQubeScanner'
		 withSonarQubeEnv() 
		 { 
      		sh "${sqScannerHome}/bin/sonar-scanner -X \
      		-Dsonar.projectKey=helloworld-ws:'kshevchenko' \
      		-Dsonar.language=java \
      		-Dsonar.java.binaries=*/target/classes"
							}
		}
}
