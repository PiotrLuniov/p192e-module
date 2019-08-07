node {
	def MAVEN_VERSION = 'Maven 3.6.1'
	def MAVEN_CONFIG = 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac'
	def STUDENT = 'mmarkova'

	stage('Preparation') {
		checkout([$class: 'GitSCM', branches: [[name: "*/${STUDENT}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p192e-module']]])
	}
	stage('Building code') {
		git branch: "$STUDENT", url: 'https://github.com/MNT-Lab/p192e-module'
 
    	withMaven(
	        maven: "$MAVEN_VERSION", 
	        globalMavenSettingsConfig: "$MAVEN_CONFIG") { 
	      		sh "mvn -f helloworld-ws/pom.xml package"
			}
	}
	stage('Sonar scan') {
		def sqScannerHome = tool 'SonarQubeScanner'
		 withSonarQubeEnv() { 
      		sh "${sqScannerHome}/bin/sonar-scanner -X \
      		-Dsonar.projectKey=helloworld-ws:${STUDENT} \
      		-Dsonar.language=java \
      		-Dsonar.java.binaries=*/target/classes"
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
    	build job: "MNTLAB-${STUDENT}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "${STUDENT}")], wait: true
    	copyArtifacts filter: 'jobs.groovy', projectName: "MNTLAB-${STUDENT}-child1-build-job"
    }
    stage('Packaging and Publishing results') {
    	parallel (
    		'archive': {
    			sh "tar -czf pipeline-$STUDENT-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile helloworld-ws/target/helloworld-ws.war"
      			archiveArtifacts "pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz"
				nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [
					[$class: 'MavenPackage', mavenAssetList: [[filePath: '${WORKSPACE}/pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz']], 
						mavenCoordinate: [
							artifactId: '${STUDENT}-tar-gz', 
							groupId: 'mnt-group', 
							packaging: 'tar.gz', 
							version: '1.0'
						]
					]
				]
    		},
    		'create Docker image': {
    			sh "docker build -t helloworld-${STUDENT}:$BUILD_NUMBER ."
    			withDockerRegistry(credentialsId: 'nexus', toolName: 'dockerTool', url: 'http://localhost:6566') {
		    		sh "docker push helloworld-${STUDENT}:${BUILD_NUMBER}"
		    	}
    		}
    	)
    }
 //    stage('Asking for manual approval') {

 //    }
 //    stage('Deployment (rolling update, zero downtime') {

 //    }
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
	catch(all) {
		echo "${command} failure"
	}
}
