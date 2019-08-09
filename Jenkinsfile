node('Host-Node') 
{
	def studentName = "kshevchenko"
	
	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https:github.com/MNT-Lab/p192e-module.git'
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
									

									
stage('Triggering job'){
    build job: 'MNTLAB-kshevchenko-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'kshevchenko')], wait: true
}
// stage('Packaging and Publishing results')
// 		{
//     copyArtifacts(projectName: 'MNTLAB-kshevchenko-child1-build-job')
//     sh 'tar xzvf kshevchenko_dsl_script.tar.gz && ls'
// 		} 
stage('Packaging and Publishing results') {
		parallel(
			'Create archieve': {
				sh 'tar czf pipeline-kshevchenko-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war'
				nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[filePath: "pipeline-kshevchenko-\${BUILD_NUMBER}.tar.gz"]], mavenCoordinate: [artifactId: "kshevchenko", groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]]
	},
			'Create docker image and push it': {
				withDockerRegistry(credentialsId: 'nexus', toolName: 'dockerTool', url: 'http://localhost:6566') {
					sh "docker build -t localhost:6566/helloworld-kshevchenko:${BUILD_NUMBER} -f Dockerfile ."
					sh "docker push localhost:6566/helloworld-kshevchenko:${BUILD_NUMBER}"
 				}			
		}
		)
}
}								
									