node('Host-Node'){
	def studentName = 'adalimayeu'
	stage('Preparation (Checking out)'){
		try {
			git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
		} catch (err) {
			emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
		}
	}

	// stage('Create health page'){
	// 	try {
	// 		sh 'sed -i "s/_version_/${BUILD_NUMBER}/g" config/test.html'
	// 		sh 'sed -i "s/_COMMIT_/$(git rev-parse HEAD)/g" config/test.html'
	// 		sh 'sed -i "s/_date_/$(date)/g" config/test.html'
	// 		sh 'cp config/test.html helloworld-ws/src/main/webapp/test.html'
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	// stage('Building code'){
	// 	try {
	// 		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
	// 			sh 'mvn clean package -f helloworld-ws/pom.xml'
	// 		}
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	// stage('Sonar scan'){
	// 	try {
	// 		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
	// 			withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
	// 				sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -f helloworld-ws/pom.xml -Dsonar.projectKey=adalimayeu_helloworld -Dsonar.projectName=adalimayeu_helloworld"
	// 			}
	// 		}
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	// stage('Testing'){
	// 	try {
	// 		parallel 'pre-integration-test': {
	// 				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
	//    					sh 'mvn pre-integration-test -f helloworld-ws/pom.xml'
	// 				}
	// 			},
	// 			'integration-test': {
	// 				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
	//    					sh 'mvn integration-test -f helloworld-ws/pom.xml'
	// 				}
	// 			},
	// 			'post-integration-test': {
	// 				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') {
	//    					sh 'mvn post-integration-test -f helloworld-ws/pom.xml'
	// 				}
	// 			}
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	// stage('Triggering job and fetching artefact after finishing'){
	// 	try {
	// 		build job: "MNTLAB-${studentName}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "${studentName}")], wait: true
	// 		copyArtifacts filter: "${studentName}_dsl_script.tar.gz", fingerprintArtifacts: true, projectName: "EPBYMINW9138/MNTLAB-${studentName}-child1-build-job", selector: lastSuccessful()
	// 		sh "tar -xzf ${studentName}_dsl_script.tar.gz"
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	// stage('Packaging and Publishing results'){
	// 	try {
	// 		parallel 'Archiving artifact': {
	// 				sh "tar czf pipeline-${studentName}-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war"
	// 				nexusPublisher nexusInstanceId: 'nexus', 
	// 					nexusRepositoryId: 'MNT-pipeline-training', 
	// 					packages: [[$class: 'MavenPackage', 
	// 						mavenAssetList: [[classifier: '', extension: '', filePath: "pipeline-${studentName}-\${BUILD_NUMBER}.tar.gz"]], 
	// 						mavenCoordinate: [artifactId: "${studentName}", groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]
	// 					]
	// 			},
	// 			'Creating Docker Image': {
	// 				withDockerRegistry(credentialsId: 'nexus', url: 'http://localhost:6566') {
	// 					sh "docker build -t localhost:6566/helloworld-${studentName}:${BUILD_NUMBER} -f config/Dockerfile ."
	// 					sh "docker push localhost:6566/helloworld-${studentName}:${BUILD_NUMBER}"
	// 				}
	// 			}
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	// stage('Asking for manual approval'){
	// 	try {
	// 		timeout(time: 2, unit: 'MINUTES') {
	// 			input(id: "Deploy artifact", message: "Deploy helloworld-${studentName}:${env.BUILD_NUMBER}?", ok: 'Deploy')
	// 		}
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	// stage('Deployment'){
	// 	try {
	// 		node('HBLEDAI_kubectl'){
	// 			sh "wget https://raw.githubusercontent.com/MNT-Lab/p192e-module/${studentName}/config/hello_k8s.yml"
	// 			sh "sed -i \"s/_studentName_/${studentName}/g\" hello_k8s.yml"
	// 			sh 'sed -i "s/_buildNumber_/${BUILD_NUMBER}/g" hello_k8s.yml'

	// 			sh "kubectl apply --namespace=${studentName} -f hello_k8s.yml"
	// 		}
	// 	} catch (err) {
	// 		emailext body: "$err.getMessage()", subject: 'Errors in the Pipeline ', to: 'alex.dalimaev@yandex.by'
	// 	}
	// }

	post {
		echo "Test post stage"
		echo "${currentBuild.result}"
	}
}