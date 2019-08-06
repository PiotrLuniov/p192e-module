node('Host-Node') {
	def studentName = 'ashamchonak'
	
	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}


	stage('Building code'){
		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
			sh "mvn clean package -f helloworld-ws/pom.xml"
		}
        }

	stage('SonarQube analysis') {
	    withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac', installationName: 'SonarQubeScanner') {
		sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.1744:sonar'
	    }
	}

	stage('SonarQube analysis'){
		def scannerHome = tool 'SonarQube Scanner 4.0';
		withSonarQubeEnv('SonarQubeScanner') {	
    		sh "${scannerHome}/bin/sonar-scanner"
		}
	}
	
//	stage('Triggering job and fetching artefact after finishing'){
//		echo "Triggering job and fetching artefact after finishing"
//	}
//	stage('Packaging and Publishing results'){
//		echo "Packaging and Publishing results"
//	}
//	stage('Asking for manual approval'){
//		echo "Asking for manual approval"
//	}
//	stage('Deployment'){
//		echo "Deployment"
//	}

}
