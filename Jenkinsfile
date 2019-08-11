def MAVEN_VERSION = 'Maven 3.6.1'
def MAVEN_CONFIG = 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac'
def STUDENT = 'mmarkova'
def DEFAULT_RES = 'FAILURE'
def ERR_MSG

node {
	stage('Preparation') {
		try {
			checkout([$class: 'GitSCM', branches: [[name: "*/${STUDENT}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p192e-module']]])
		}
		catch(err) {
			echo ("something goes wrong")
			emailReport('Preparation', err.getMessage(), ${DEFAULT_RES})
		}
	}

	stage('Building code') {
		try {
		git branch: "$STUDENT", url: 'https://github.com/MNT-Lab/p192e-module'
 
    	withMaven(
	        maven: "$MAVEN_VERSION", 
	        globalMavenSettingsConfig: "$MAVEN_CONFIG") { 
	      		sh "mvn -f helloworld-ws/pom.xml package"
			}
		}
		catch(err) {
      		echo ("something goes wrong")
      		emailReport('Building', err.getMessage(), ${DEFAULT_RES})
      	}
	}

	stage('Sonar scan') {
		try {
			def sqScannerHome = tool 'SonarQubeScanner'
			 withSonarQubeEnv() { 
	      		sh "${sqScannerHome}/bin/sonar-scanner -X \
	      		-Dsonar.projectKey=helloworld-ws:${STUDENT} \
	      		-Dsonar.language=java \
	      		-Dsonar.java.binaries=*/target/classes"
	      	}
      	}
      	catch(Exception e) {
      		emailReport('Sonar scan', e.getMessage(), ${DEFAULT_RES})
      	}
      	catch(err) {
      		emailReport('Sonar scan', err.getMessage(), ${DEFAULT_RES})
      	}
      	finally {
      		echo ("something goes wrong")
      	}
	}

	stage('Testing') {
        parallel (
            'pre-integration test': {
            	test('pre-integration-test')
            }, 
            'integration test': {
            	test('integration-test')
            },
            'post-integration test': {
            	test('post-integration-test')
            }
        )
    }

    stage('Triggering job and fetching artefact after finishing') {
    	try {
	    	build job: "MNTLAB-${STUDENT}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "${STUDENT}")], wait: true
	    	copyArtifacts filter: 'jobs.groovy', projectName: "MNTLAB-${STUDENT}-child1-build-job"
    	}
    	catch(err) {
    		echo ("something goes wrong")
    		emailReport('Triggering', err.getMessage(), ${DEFAULT_RES})
    	}
    }

    stage('Packaging and Publishing results') {
    	parallel (
    		'archive': {
    			try {
	    			sh "tar -czf pipeline-$STUDENT-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile helloworld-ws/target/helloworld-ws.war"
	      			archiveArtifacts "pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz"
					nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [
						[$class: 'MavenPackage', mavenAssetList: [[filePath: "${WORKSPACE}/pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz"]], 
							mavenCoordinate: [
								artifactId: "${STUDENT}", 
								groupId: 'pipeline', 
								packaging: 'tar.gz', 
								version: "${BUILD_NUMBER}"
							]
						]
					]
				}
				catch(err) {
		      		echo ("something goes wrong")
		      		emailReport('Archive', err.getMessage(), ${DEFAULT_RES})
		      	}
    		},
    		'create Docker image': {
    			try {
	    			withDockerRegistry(credentialsId: 'nexus', toolName: 'dockerTool', url: 'http://localhost:6566') {
	    				sh "docker build -t localhost:6566/helloworld-${STUDENT}:$BUILD_NUMBER ."
			    		sh "docker push localhost:6566/helloworld-${STUDENT}:${BUILD_NUMBER}"
			    	}
			    }
			    catch(err) {
		      		echo ("something goes wrong")
		      		emailReport('Docker image creation', err.getMessage(), ${DEFAULT_RES})
		      	}
    		}
    	)
    }

    stage('Asking for manual approval') {
		timeout(time: 1, unit: 'MINUTES') {
		input(id: 'Deployment of artifact', message: 'I wonder if you would like to continue?', ok: 'Yes')
		}
    }

    stage('Deployment (rolling update, zero downtime') {
    	try {
	    	sh "${HOME}/kubectl apply -f app.yml"
			def proc = "curl -X HEAD -I http://nexus-ci.playpit.by/repository/docker/v2/helloworld-mmarkova/manifests/${BUILD_NUMBER}"
			           .execute().text
			sh """
				cat << EOF > check.html
				<h1>${proc}</h1>
				EOF
			"""
			emailReport('Deployment', 'all right', 'SUCCESS')
		}
		catch(err) {
      		echo ("something goes wrong")
      		emailReport('Deployment', err.getMessage(), ${DEFAULT_RES})
      	}
    }
}

def test(command) {
	try {
    	git branch: "${STUDENT}", url: 'https://github.com/MNT-Lab/p192e-module'
    	withMaven(
	        maven: "${MAVEN_VERSION}",
	        globalMavenSettingsConfig: "${MAVEN_CONFIG}") {
    			dir('helloworld-ws') {
	      			sh "mvn ${command}"
	      	}
		}
	}
	catch(err) {
		echo "${command} failure"
		emailReport("${command}", err.getMessage(), ${DEFAULT_RES})
	}
}

def emailReport(stage, what, result) {
	def date = new Date()
	def text = "Email ${result} report: \n\t" + 
			   "Job:           ${JOB_NAME} \n\t" +
			   "Stage:         ${stage} \n\t" +
			   "Date: 		   ${date} \n\t" +
			   "Build number:  ${BUILD_NUMBER} \n\t" +
			   "What's wrong:  ${what} \n\t" +
			   "Final result:  ${result}"
	emailext (
		subject: "Report from [Jenkins]",
		body: "${text}",
		recipientProviders: [brokenBuildSuspects(),developers()],
		to: 'marksuree@mail.ru'
    )  
}

