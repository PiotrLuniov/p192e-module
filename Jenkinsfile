node('Host-Node'){
	def studentName = 'adalimayeu'
	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}
	stage('Building code'){
		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
    		sh 'mvn clean package -f helloworld-ws/pom.xml'
		}
	}
	stage('Sonar scan'){
		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
    		withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
    			def sonarOptions = '-Dsonar.projectKey=adalimayeu:helloworld -Dsonar.projectName=adalimayeu:helloworld -Dsonar.sources=helloworld-ws/src/main/java -Dsonar.java.binaries=**/target/classes -Dsonar.language=java'
    			sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -f helloworld-ws/pom.xml ${sonarOptions}"
			}
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