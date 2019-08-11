node('Host-Node') {
    stage('Checkout GitHub Repository') {
        git branch: 'iyaruk',
            url: 'https://github.com/MNT-Lab/p192e-module.git'
    }

    stage('Build Project') {
         withMaven(maven: 'Maven 3.6.1') {
            sh 'mvn clean -f helloworld-ws/pom.xml package'
        }
    }
    stage('Sonar Scan') {
    def scannerHome = tool 'SonarQubeScanner';
    withSonarQubeEnv() {
    sh "${scannerHome}/bin/sonar-scanner \
               -Dsonar.projectName=iyaruk-helloworld \
               -Dsonar.projectKey=iyaruk-helloworld \
               -Dsonar.language=java \
               -Dsonar.sources=helloworld-ws/src \
               -Dsonar.java.binaries=**/target/classes"
        }
    }
    
   stage('Tests') {
        withMaven(maven: 'Maven 3.6.1',) {
            parallel (
                '1 - Pre-Int': {
                   sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                   },
               '2 - Int': {
                    sh 'mvn -f helloworld-ws/pom.xml integration-test'
                   },
                '3 - Post-Int': {
                    sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
                   }
            
            )
        }

     stage('Triggering and fetching && Publishing'){
        build job: 'MNTLAB-iyaruk-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'iyaruk')], wait: true
        copyArtifacts filter: 'output.txt', flatten: true, projectName: 'MNTLAB-iyaruk-child1-build-job', selector: workspace()
        sh "tar -czvf pipeline-iyaruk-\${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war"
				
         nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', \
			packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', \
			filePath: "pipeline-iyaruk-\${BUILD_NUMBER}.tar.gz"]], \
		    mavenCoordinate: [artifactId: "iyaruk", groupId: 'pipeline', \
			packaging: 'tar.gz', version: '${BUILD_NUMBER}'] \
                ]]
    }
    }

	stage('Creating Docker Image') {
		withDockerRegistry(credentialsId: 'nexus', toolName: 'dockerTool', url: 'https://registry-ci.playpit.by') {
		sh '''
		docker login -u iyaruk -p iyaruk1234 registry-ci.playpit.by
		docker build -t registry-ci.playpit.by/helloworld-iyaruk:${BUILD_NUMBER} -f Dockerfile .
		docker push registry-ci.playpit.by/helloworld-iyaruk:${BUILD_NUMBER}
		'''
		}

	stage('Manual approval'){
		timeout(time: 1, unit: 'MINUTES') {
			input(id: "Deployment artifact", \
			      message: "Do you mind to deploy helloworld-iyaruk:${env.BUILD_NUMBER}?", \
			      ok: "Yes, I do.")
				}
			}

	stage('Kubernetes Deployment'){
		sh '''
		echo "Deployment with new build number"
		sed -i "s/NUMBER/${BUILD_NUMBER}/g" k8-sett/'1 - dep.yml'
		$HOME/kubectl apply -f k8-sett/'1 - dep.yml'
		sleep 5
		$HOME/kubectl apply -f k8-sett/'2 - svc.yml'
		sleep 5
		$HOME/kubectl apply -f k8-sett/'3 - ing.yml'
		'''
	}
	stage ('Checking') {
       		try {
            		sh "curl http://iyaruk-app.k8s.playpit.by/ | grep 'helloworld-ws Quickstart'"
			echo "Simple check with curl passed with SUCCESS!"
			return true
      			} catch (resp) {
        			echo "Simple check with curl failured!"
        			currentBuild.result = 'FAILURE'
        			
				}
    		}
		
	currentBuild.result = 'SUCCESS'
	echo "BUILD_SUCCESS"
	mail bcc: '', body: 'BUILD_SUCCESS<br>Project:MNTLAB_Jenkins<br>BUILD_NUMBER:${BUILD_NUMBER}', cc: '', from: '', replyTo: '', subject: 'Success', to: 'buagir1990@gmail.com'
	}
	
	catch (err) {
		currentBuild.result = 'FAILURE'
		echo "JOB FAILURE"
		mail bcc: '', body: 'BUILD_FAILURE<br>Project:MNTLAB_Jenkins<br>BUILD_NUMBER:${BUILD_NUMBER}<br>Errors:${err}', cc: '', from: '', replyTo: '', subject: 'Fail_!', to: 'buagir1990@gmail.com'
  }

}

}
}
