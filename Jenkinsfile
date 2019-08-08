
node(){
	stage('Checking out'){
		git branch: "kkaminski", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}
	stage('Building code'){
		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1', mavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac'){
			sh 'mvn clean package -f helloworld-ws/pom.xml' 
		}
	}
	// stage('Sonar scan') {
	// 	withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
	// 	withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
	// 		sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -f helloworld-ws/pom.xml ' +
	// 		'-Dsonar.projectKey=kkaminski ' +
	// 		'-Dsonar.projectName=kkaminski '
	// 		}
	// 	}
	// }
 //    stage('Testing') {
 //    	parallel(
 //    		'pre-integration-test': {
 //    			withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1', mavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac') {
 //    				sh 'mvn pre-integration-test -f helloworld-ws/pom.xml' 
 //    			}
 //    		},
 //    		'integration-test': {
 //    			withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1', mavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac') {
 //    				sh 'mvn integration-test -f helloworld-ws/pom.xml' 
 //    			}
 //    		},
 //    		'post-integration-test': {
 //    			withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1', mavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac') {
 //    				sh 'mvn post-integration-test -f helloworld-ws/pom.xml' 
 //    			}
 //    		}
 //    	)
	// }
	stage('Triggering job and fetching artefact after finishing'){
		build job: "MNTLAB-kkaminski-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "kkaminski")], wait: true
		copyArtifacts filter: "kkaminski_dsl_script.tar.gz", fingerprintArtifacts: true, projectName: "MNTLAB-kkaminski-child1-build-job", selector: lastSuccessful()
		sh "tar -xzf kkaminski_dsl_script.tar.gz && ls"
		}
	stage('Packaging and Publishing results') {
		parallel(
			'Create archieve': {
				sh 'tar czf pipeline-kkaminski-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war'
				nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[filePath: "pipeline-kkaminski-\${BUILD_NUMBER}.tar.gz"]], mavenCoordinate: [artifactId: "kkaminski", groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]]
	},
			'Create docker image and push it': {
				withDockerRegistry(credentialsId: 'nexus', toolName: 'dockerTool', url: 'http://localhost:6566') {
					sh "docker build -t localhost:6566/helloworld-kkaminski:${BUILD_NUMBER} -f Dockerfile ."
					sh "docker push localhost:6566/helloworld-kkaminski:${BUILD_NUMBER}"
 				}			
			}
		)
	}
		stage('Asking for manual approval') {
			timeout(time: 1, unit: 'MINUTES') {
			input(id: 'Deployment of artifact', message: 'Deploying the current artifact?', ok: 'Continue')
		}
	}
		stage('Deployment') {
		node('HBLEDAI_kubectl'){
			sh "kubectl apply --namespace=kkaminski -f k8s-deploy.yml"
		}
	}
}
