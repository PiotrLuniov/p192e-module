node('Host-Node'){
	def studentName = 'adalimayeu'
	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}
	stage('Building code'){
		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
    		sh 'mvn clean package -f helloworld-ws/pom.xml'
		}
	}
	stage('Sonar scan'){
		withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
    		sh 'mvn sonar:sonar -f helloworld-ws/pom.xml'
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