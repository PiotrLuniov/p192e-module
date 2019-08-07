node('Host-Node') {
	def studentName = "ashamchonak"
	def folderName = "EPBYMINW5961"
	
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
		
		copyArtifacts filter: "${studentName}_dsl_script.tar.gz", fingerprintArtifacts: true, \
			projectName: "${folderName}", selector: lastSuccessful()
		
		sh "tar -xvf ${studentName}_dsl_script.tar.gz jobs.groovy"
		
		
//		//triggerRemoteJob abortTriggeredJob: true, job: 'MNTLAB-ashamchonak-child1-build-job', \
//			maxConn: 1, parameters: 'BRANCH_NAME=ashamchonak', remoteJenkinsUrl: 'localhost', \
//			useCrumbCache: true, useJobInfoCache: true
		
		echo "Triggering job and fetching artefact after finishing"
	}

	
	
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
