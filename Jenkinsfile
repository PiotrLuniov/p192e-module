@Library('ashamchonak_library') _

node('Host-Node') {
    try {	
	def studentName = "ashamchonak"

	stage('Preparation (Checking out)'){
		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}

	stage('BuildNumber page'){
		sh """
cat << EOF > helloworld-ws/src/main/webapp/version.html
<html><head><title>Build:${BUILD_NUMBER}</title></head>
<body><header><h1>Build:${BUILD_NUMBER}</h1></header></html>
EOF
		"""
	}
	    
	stage('Building code'){
		withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
			jdk: 'JDK9', maven: 'Maven 3.6.1') {
			sh "mvn clean package -f helloworld-ws/pom.xml"
		}
	}

	stage('Sonar scan') {
		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
			withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
				sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar \
				-f helloworld-ws/pom.xml \
				-Dsonar.projectKey=${studentName} \
				-Dsonar.projectName=${studentName} \
				-Dsonar.projectVersion=1.0 \
				-Dsonar.language=java \
				-Dsonar.sourceEncoding=UTF-8 \
				-Dsonar.login=${studentName} \
				-Dsonar.password=ashamchonak \
				"
			}
	    	}
	}

	stage('Testing') {
		parallel (
			'pre-integration-test': {
				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
					jdk: 'JDK9', maven: 'Maven 3.6.1') {
					sh "mvn pre-integration-test -f helloworld-ws/pom.xml"
				}

			},

			'integration-test': {
				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
					jdk: 'JDK9', maven: 'Maven 3.6.1') {
					sh "mvn integration-test -f helloworld-ws/pom.xml"
				}

			},

			'post-integration-test': {
				withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', \
					jdk: 'JDK9', maven: 'Maven 3.6.1') {
					sh "mvn post-integration-test -f helloworld-ws/pom.xml"
				}

			}
		)
	}

	stage('Triggering job and fetching artefact after finishing'){
		build job: "MNTLAB-${studentName}-child1-build-job", \
			parameters: [string(name: 'BRANCH_NAME', value: "${studentName}")], \
			wait: true, propagate: true
		copyArtifacts filter: "output.txt", fingerprintArtifacts: true, \
			projectName: "MNTLAB-${studentName}-child1-build-job", selector: lastSuccessful()
	}

	stage('Packaging and Publishing results'){
		parallel (
			'Archiving artifact': {
				copyArtifacts filter: "output.txt", fingerprintArtifacts: true, \
					projectName: "MNTLAB-${studentName}-child1-build-job", selector: lastSuccessful()
				sh "rm -rf pipeline-${studentName}-*.tar.gz"
				sh "cp -f helloworld-ws/target/helloworld-ws.war helloworld-ws.war"

				sh "tar -czvf pipeline-${studentName}-\${BUILD_NUMBER}.tar.gz \
					output.txt Jenkinsfile helloworld-ws.war"

                megaPush("maven", studentName)

			},

			'Creating Docker Image': {

                megaPush("docker", studentName)

			}
		)
    	echo "Packaging and Publishing results"
	}

	stage('Asking for manual approval'){
	timeout(time: 2, unit: 'MINUTES') {
			input(id: "Deployment artifact", \
			      message: "Wouldn\'t you mind to deploy helloworld-${studentName}:${BUILD_NUMBER}?", \
			      ok: "I wouldn\'t mind.")
		}
	}
 
	stage('Deployment'){
		sh """
sed -i "s/STUDENT_NAME/${studentName}/g" tomcat/tomcat-ns.yaml
$HOME/kubectl apply -f tomcat/tomcat-ns.yaml

sed "s/BUILD_NUMBER/${BUILD_NUMBER}/g" tomcat/tomcat-dep.yaml > tomcat-dep.yaml
sed -i "s/STUDENT_NAME/${studentName}/g" tomcat-dep.yaml
$HOME/kubectl apply -f tomcat-dep.yaml
sleep 15

sed "s/STUDENT_NAME/${studentName}/g" tomcat/tomcat-s-i.yaml > tomcat-s-i.yaml
$HOME/kubectl apply -f tomcat-s-i.yaml

echo "Deployment  END"
		"""
	}
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
