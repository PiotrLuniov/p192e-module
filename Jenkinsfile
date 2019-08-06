
node(){
	stage('Checking out'){
		git branch: "kkaminski", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}
	stage('Building code'){
		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1', MavenSettingsConfig: 'MNT-group nexus-ci'){
			sh 'mvn clean package -f helloworld-ws/pom.xml' 
		}
	}
	stage('Sonar scan') {
		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
		withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
			sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -f helloworld-ws/pom.xml ' +
			'-Dsonar.projectKey=helloworld-kkaminski ' +
			'-Dsonar.projectName=helloworld-kkaminski '
		}
		}
	}
}
//     }

//     stage('Testing') {
//         sh 'make publish'
//     }
 
//     stage('Triggering job and fetching artefact after finishing') {
//         sh 'make publish'
//     }

//     stage('Packaging and Publishing results') {
//         sh 'make publish'
//     }

//     stage('Asking for manual approval') {
//         sh 'make publish'
//     }

//     stage('Deployment') {
//         sh 'make publish'
//     }
// } 

