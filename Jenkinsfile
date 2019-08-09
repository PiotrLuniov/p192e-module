node('Host-Node')
{
try { 	
   stage('Preparation') 
{
      git branch: 'kshevchenko', url: 'https://github.com/MNT-Lab/p192e-module.git'
}
  
stage('BuildNumber page'){
		sh """
cat << EOF > helloworld-ws/src/main/webapp/deploy.html
<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <title>Build:${BUILD_NUMBER}</title>
 </head> 
 <body>
  <header>
    <h1>Maintainer "Kirill Shevchenko"</h1>
  </header>
  <article>
    <h2>WELCO!</h2>
    <p>Build:${BUILD_NUMBER}</p>
  </article>
  <footer>
    Copyright Kirill Shevchenko
  </footer>
 </body> 
</html>
EOF
		"""
	}
	
	
	
  stage('Building code')
{
			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') 
{
				sh 'mvn clean package -f helloworld-ws/pom.xml'
stage('Sonar scan'){
			withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
				withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
					sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -f helloworld-ws/pom.xml -Dsonar.projectKey=kshevchenko -Dsonar.projectName=kshevchenko_helloworld"
				}
}
}
}
	// stage('Testing') {
	// 	parallel (
	// 		'pre-integration-test': {
	// 			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
	// 				jdk: 'JDK9', maven: 'Maven 3.6.1') {
	// 				sh "mvn pre-integration-test -f helloworld-ws/pom.xml"
	// 			}

	// 		},

	// 		'integration-test': {
	// 			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
	// 				jdk: 'JDK9', maven: 'Maven 3.6.1') {
	// 				sh "mvn integration-test -f helloworld-ws/pom.xml"
	// 			}

	// 		},

	// 		'post-integration-test': {
	// 			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
	// 				jdk: 'JDK9', maven: 'Maven 3.6.1') {
	// 				sh "mvn post-integration-test -f helloworld-ws/pom.xml"
	// 			}

	// 		}
	// 	)
	// }
	
	
	
	stage('Triggering job'){
    build job: 'MNTLAB-kshevchenko-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'kshevchenko')], wait: true
}
stage('Packaging and Publishing results')
		{
    copyArtifacts(projectName: 'MNTLAB-kshevchenko-child1-build-job')
    sh 'tar xzvf kshevchenko_dsl_script.tar.gz && ls'
		} 
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


	
	
// stage('Asking for manual approval'){
// 	timeout(time: 2, unit: 'MINUTES') {
// 			input(id: "Deployment artifact", \
// 			      message: "Wouldn\'t you mind to deploy helloworld-'kshevchenko':${BUILD_NUMBER}?", \
// 			      ok: "I wouldn\'t mind.")
// 		}
// }		
	
	
}	
catch (err) {
		println "The build ${BUILD_NUMBER} has failed with error:\n${err}"
		emailext body: "The build ${BUILD_NUMBER} has failed with error:\n${err}", \
			recipientProviders: [developers()], subject: "Build ${BUILD_NUMBER} failed", \
			to: "studen2devops@gmail.com"
		currentBuild.result = 'FAILURE'
	}

	finally {
		if(currentBuild.result == 'SUCCESS'){
			echo "The build ${BUILD_NUMBER} has done successfully"
			emailext body: "The build ${BUILD_NUMBER} has done successfully", \
				recipientProviders: [developers()], subject: "Build ${BUILD_NUMBER} success", \
				to: "studen2devops@gmail.com"
		}

	
	}	
}
