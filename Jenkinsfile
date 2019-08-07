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
		copyArtifacts filter: "${studentName}_dsl_script.tar.gz", fingerprintArtifacts: true, projectName: "EPBYMINW9138/MNTLAB-${studentName}-child1-build-job", selector: lastSuccessful()
		sh "tar -xzf ${studentName}_dsl_script.tar.gz"
	}

	stage('Packaging and Publishing results'){
		parallel 'Archiving artifact': {
				sh "tar czf pipeline-${studentName}-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war"
				nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: "pipeline-${studentName}-\${BUILD_NUMBER}.tar.gz"]], mavenCoordinate: [artifactId: "${studentName}", groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]]
			},
			'Creating Docker Image': {
				def createDockerfile = 	"cat << EOF > Dockerfile\n" + 
										"FROM tomcat:8.0\n" +
										"COPY helloworld-ws/target/helloworld-ws.war /usr/local/tomcat/webapps/\n" +
										"CMD [\"/usr/local/tomcat/bin/catalina.sh\", \"run\"]\n" +
										"EOF"
				
				sh "${createDockerfile}"

				withDockerServer([uri: 'tcp://docker-in-docker:2375']) {
					withDockerRegistry(credentialsId: 'nexus', url: 'http://nexus-ci.playpit.by:6566/helloworld-adalimayeu:55') {
					    def image = docker.build("<http://nexus-ci.playpit.by:6566/helloworld-adalimayeu:55")
						image.push()
					}
				}
				//buildImage email: '', name: "helloworld-${studentName}:42", password: '', path: '', rm: false, timeout: 0, username: ''
			}
	}
	stage('Asking for manual approval'){
		echo "Asking for manual approval"
	}
	stage('Deployment'){
		echo "Deployment"
	}

}