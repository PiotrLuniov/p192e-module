
// node ('Host-Node') { 

// 	stage ('Apavarnitsyn-Maven-Build') {
 			
// sh """ 

// cat << EOF > helloworld-ws/src/main/webapp/test.html
//  <!DOCTYPE html>
// <html>
// <body>
// <h3>Healt page</h3>
// <p>version ${env.BUILD_NUMBER}</p>
// <p>Author: Andrey Pavarnitsyn</p>
// <p>Date: Andrey Pavarnitsyn</p>
// </body>
// </html> 
// EOF
//  """	
// 	withMaven(
//     jdk: 'JDK9',
//     maven: 'Maven 3.6.1', 
//     mavenSettingsConfig: 'Maven2-Nexus-Repos') { 

//  		sh "mvn -f helloworld-ws/pom.xml clean package" 
//  		sh "cp helloworld-ws/target/helloworld-ws.war hello.war"

//  		}	
//   }	


//   stage('Sonar-scan') {
//     def scannerHome = tool 'SonarQubeScanner';
//     withSonarQubeEnv() {
//        sh "${scannerHome}/bin/sonar-scanner " +
//        '-Dsonar.projectKey=apavarnitsyn ' +
//        '-Dsonar.language=java ' +
//        '-Dsonar.sources=helloworld-ws/src/main/java  '+
//        '-Dsonar.java.binaries=**/target/classes'
//     }
//   } 

//   stage('run-parallel-mvn-tests') {
//     parallel(
// 	    a: {
// 	    withMaven(
// 	        jdk: 'JDK9',
// 	        maven: 'Maven 3.6.1', 
// 	        mavenSettingsConfig: 'Maven2-Nexus-Repos') {
// 	    	sh "mvn pre-integration-test -f helloworld-ws/pom.xml"
// 	    	}
// 	    },
// 	    b: {
// 	    withMaven(
// 	        jdk: 'JDK9',
// 	        maven: 'Maven 3.6.1', 
// 	        mavenSettingsConfig: 'Maven2-Nexus-Repos') {	
// 	        sh "mvn integration-test -f helloworld-ws/pom.xml"
// 	    	}
// 	    },
// 	    c: {
// 	    withMaven(
// 	        jdk: 'JDK9',
// 	        maven: 'Maven 3.6.1', 
// 	        mavenSettingsConfig: 'Maven2-Nexus-Repos') {
// 	        sh "mvn post-integration-test -f helloworld-ws/pom.xml"
// 	        }
// 		}
//     )
        
//     }



//   stage('Triggering job'){
//     build job: 'MNTLAB-apavarnitsyn-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'apavarnitsyn')], wait: true
// }
//   stage('Packaging and Publishing results'){
//     copyArtifacts(projectName: 'MNTLAB-apavarnitsyn-child1-build-job')
//     sh 'tar xzvf apavarnitsyn_dsl_script.tar.gz && ls'
// } 
//   stage('Build-Docker-image') {



//   	stage('Packaging and Publishing results'){
// 		parallel 'Packaging': {
// 				sh "tar czf pipeline-apavarnitsyn-${env.BUILD_NUMBER}.tar.gz output.txt Jenkinsfile hello.war"
// 				nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: "pipeline-apavarnitsyn-\${BUILD_NUMBER}.tar.gz"]], mavenCoordinate: [artifactId: 'apavarnitsyn', groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]]
// 			},
// 			'Publishing Docker Image': {
//                 docker.withRegistry('http://localhost:6566', 'nexus') {
//                     def appImage = docker.build("localhost:6566/helloworld-apavarnitsyn:${env.BUILD_NUMBER}", '-f files/Dockerfile .')
//                     appImage.push()
//                 }
// 			}

// 	}
//   }
// 	stage('Asking for manual approval') {
// 			timeout(time: 1, unit: 'MINUTES') {
// 			input(id: 'Deployment of artifact', message: 'Deploying the current artifact?', ok: 'Continue')
// 		}
// }
// }

node ('k8s-slave') { 
	stage ('Apavarnitsyn-Checkout') {
    git branch: "apavarnitsyn", url: 'https://github.com/MNT-Lab/p192e-module.git'

	}

  stage ('Apavarnitsyn-CD-Kubectl - Build') {
 	
sh """
cd files/
./script.sh ${env.BUILD_NUMBER}
"""
	}
}
