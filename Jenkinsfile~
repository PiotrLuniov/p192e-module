node('Host-Node') 
{
	def studentName = "kshevchenko"
	
	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
													}

	stage('Building code'){
		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
			jdk: 'JDK9', maven: 'Maven 3.6.1') {
			sh "mvn clean package -f helloworld-ws/pom.xml"
									}
															}

stage('Sonar scan') {
		def sqScannerHome = tool 'SonarQubeScanner'
		 withSonarQubeEnv() 
		 { 
      		sh "${sqScannerHome}/bin/sonar-scanner -X \
      		-Dsonar.projectKey=helloworld-ws:${studentName} \
      		-Dsonar.language=java \
      		-Dsonar.java.binaries=*/target/classes"
							}
		}
									
	stage('Testing') {
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
									
stage('Triggering job'){
    build job: 'MNTLAB-kshevchenko-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'kshevchenko')], wait: true
								}
stage('Packaging and Publishing results')
		{
    copyArtifacts(projectName: 'MNTLAB-kshevchenko-child1-build-job')
    sh 'tar xzvf kshevchenko_dsl_script.tar.gz && ls'
		} 
							
									
									
									
}
