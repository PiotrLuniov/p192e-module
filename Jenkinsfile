node {
	def studentName = 'ashamchonak'
	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}



	stage('Building code'){
		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1', mavenSettingsFilePath: 'helloworld-ws/pom.xml') {
        mvn clean package
        }
	}



	stage('Testing'){
		echo "Testing"
	}
	stage('Triggering job and fetching artefact after finishing'){
		echo "Triggering job and fetching artefact after finishing"
	}
	stage('Packaging and Publishing results'){
		echo "Packaging and Publishing results"
	}
	stage('Asking for manual approval'){
		echo "Asking for manual approval"
	}
	stage('Deployment'){
		echo "Deployment"
	}

}