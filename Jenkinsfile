node('Host-Node') {
	def studentName = "ashamchonak"
	
	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}


	stage('Building code'){
		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
			jdk: 'JDK9', maven: 'Maven 3.6.1') {
			sh "mvn clean package -f helloworld-ws/pom.xml"
		}
        }

//	stage('Sonar scan') {
//		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
//			withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
//				sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar \
//				-f helloworld-ws/pom.xml \
//				-Dsonar.projectKey=${studentName} \
//				-Dsonar.projectName=${studentName} \
//				-Dsonar.projectVersion=1.0 \
//				-Dsonar.language=java \
//				-Dsonar.sourceEncoding=UTF-8 \
//				-Dsonar.login=${studentName} \
//				-Dsonar.password=ashamchonak \
//				"
//			}
//	    	}
//	}
//
//	stage('Testing') {
//		parallel (
//			'pre-integration-test': { 
//				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
//					jdk: 'JDK9', maven: 'Maven 3.6.1') {
//					sh "mvn pre-integration-test -f helloworld-ws/pom.xml"
//				}
//
//			},
//
//			'integration-test': { 
//				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
//					jdk: 'JDK9', maven: 'Maven 3.6.1') {
//					sh "mvn integration-test -f helloworld-ws/pom.xml"
//				}
//
//			},
//
//			'post-integration-test': { 
//				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
//					jdk: 'JDK9', maven: 'Maven 3.6.1') {
//					sh "mvn post-integration-test -f helloworld-ws/pom.xml"
//				}
//
//			}
//		)
//	}
	
	stage('Triggering job and fetching artefact after finishing'){		
		build job: "MNTLAB-${studentName}-child1-build-job", \
			parameters: [string(name: 'BRANCH_NAME', value: "${studentName}")], \
			wait: true, propagate: true		
		copyArtifacts filter: "output.txt", fingerprintArtifacts: true, \
			projectName: "MNTLAB-${studentName}-child1-build-job", selector: lastSuccessful()
		sh " ls -la		"				
	}
	
	stage('Packaging and Publishing results'){

		parallel {
			agent any
			stages {
				stage('Example') {
					steps { 
						echo 'Hello World'
					}
				}
			}
		}
                
		parallel (
			'Archiving artifact': {
				
			},
			
			
			'Creating Docker Image': {
				
			}
		)
		echo "Packaging and Publishing results"
	}
	
	
	
	
//		echo "Asking for manual approval"
//	}
//	stage('Deployment'){
//		echo "Deployment"
//	}

}
