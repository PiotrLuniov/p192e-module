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
	// stage('Sonar scan'){
	// 	withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
 //    		withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
 //    			sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -f helloworld-ws/pom.xml -Dsonar.projectKey=adalimayeu_helloworld -Dsonar.projectName=adalimayeu_helloworld"
	// 		}
	// 	}
		
	// }
	// stage('Testing'){
	// 	parallel 'pre-integration-test': {
	// 			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
 //   					sh 'mvn pre-integration-test -f helloworld-ws/pom.xml'
	// 			}
	// 		},
	// 		'integration-test': {
	// 			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
 //   					sh 'mvn integration-test -f helloworld-ws/pom.xml'
	// 			}
	// 		},
	// 		'post-integration-test': {
	// 			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
 //   					sh 'mvn post-integration-test -f helloworld-ws/pom.xml'
	// 			}
	// 		}
	// }
	stage('Triggering job and fetching artefact after finishing'){
		build job: "MNTLAB-${studentName}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "${studentName}")], wait: true
		copyArtifacts filter: 'adalimayeu_dsl_script.tar.gz', fingerprintArtifacts: true, projectName: 'EPBYMINW9138/MNTLAB-adalimayeu-child1-build-job', selector: lastSuccessful()
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