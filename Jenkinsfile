
node ('Host-Node') { 
try {
def STAGE = 'Apavarnitsyn / Build';
	stage ("${STAGE}") {
sh """ 
cat << EOF > helloworld-ws/src/main/webapp/test.html
 <!DOCTYPE html>
<html>
<body>
<h3>Healt page</h3>
<p>Author: Andrey Pavarnitsyn</p>
<p>version "\${BUILD_NUMBER}"</p>
<p>Build Time: "\$(date)" </p>
</body>
</html> 
EOF
"""	
		withMaven(
	    jdk: 'JDK9',
	    maven: 'Maven 3.6.1', 
	    mavenSettingsConfig: 'Maven2-Nexus-Repos') { 
	 		sh "mvn -f helloworld-ws/pom.xml clean package" 
	 		sh "cp helloworld-ws/target/helloworld-ws.war hello.war"
	 	}	
  	}	

STAGE = 'Sonar-scan';
  	stage("${STAGE}") {
    	def scannerHome = tool 'SonarQubeScanner';
	    withSonarQubeEnv() {
	       sh "${scannerHome}/bin/sonar-scanner " +
	       '-Dsonar.projectKey=apavarnitsyn ' +
	       '-Dsonar.language=java ' +
	       '-Dsonar.sources=helloworld-ws/src/main/java  '+
	       '-Dsonar.java.binaries=**/target/classes'
	    }
  } 
try{

STAGE = 'run-parallel-Testing';
	stage("${STAGE}") {
	    parallel(
		    "pre-integration": {
		    withMaven(
		        jdk: 'JDK9',
		        maven: 'Maven 3.6.1', 
		        mavenSettingsConfig: 'Maven2-Nexus-Repos') {
		    	sh "mvn pre-integration-test -f helloworld-ws/pom.xml"
		    	}
		    },
		    "integration": {
		    withMaven(
		        jdk: 'JDK9',
		        maven: 'Maven 3.6.1', 
		        mavenSettingsConfig: 'Maven2-Nexus-Repos') {	
		        sh "mvn integration-test -f helloworld-ws/pom.xml"
		    	}
		    },
		    "post-integration": {
		    withMaven(
		        jdk: 'JDK9',
		        maven: 'Maven 3.6.1', 
		        mavenSettingsConfig: 'Maven2-Nexus-Repos') {
		        sh "mvn post-integration-test -f helloworld-ws/pom.xml"
		        }
			}
	    )
        
    }
}
catch(all) {

STAGE = 'run-raw-Testing';
	stage("${STAGE}") {
		withMaven(
	        jdk: 'JDK9',
	        maven: 'Maven 3.6.1', 
	        mavenSettingsConfig: 'Maven2-Nexus-Repos') {
sh '''
mvn pre-integration-test -f helloworld-ws/pom.xml
mvn integration-test -f helloworld-ws/pom.xml
mvn post-integration-test -f helloworld-ws/pom.xml
'''}
	}
}


STAGE = 'Triggering job';
  	stage("${STAGE}"){
    	build job: 'MNTLAB-apavarnitsyn-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'apavarnitsyn')], wait: true
	}

STAGE = 'Packaging and Publishing results';
  	stage("${STAGE} 1"){
    	copyArtifacts(projectName: 'MNTLAB-apavarnitsyn-child1-build-job')
    	sh 'tar xzvf apavarnitsyn_dsl_script.tar.gz && ls'
	} 
  	stage("${STAGE} 2"){
	parallel 'Packaging': {
		sh "tar czf pipeline-apavarnitsyn-${env.BUILD_NUMBER}.tar.gz output.txt Jenkinsfile hello.war"
			nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: "pipeline-apavarnitsyn-\${BUILD_NUMBER}.tar.gz"]], mavenCoordinate: [artifactId: 'apavarnitsyn', groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]]
		},
		'Publishing Docker Image': {
	                docker.withRegistry('http://localhost:6566', 'nexus') {
	                    def appImage = docker.build("localhost:6566/helloworld-apavarnitsyn:${env.BUILD_NUMBER}", '-f files/Dockerfile .')
	                    appImage.push()
	                }
				}

	}

	// stage('Asking for manual approval') {
	// 		timeout(time: 1, unit: 'MINUTES') {
	// 		input(id: 'Deployment of artifact', message: 'Deploying the current artifact?', ok: 'Continue')
	// 	}
 // 	}
}
catch(all) {
        def now = new Date()
        def body = "There are errors in pipeline:\n${err}\nBuild: ${env.BUILD_NUMBER}\nErrors has appeared: ${now}"
        println body
		emailext body: "${body}", recipientProviders: [developers()], subject: 'Pipeline errors!', to: 'alex.dalimaev@yandex.by'
		currentBuild.result = 'FAILURE'
}
finally {
		if(currentBuild.result == 'SUCCESS'){
			echo "Pipeline has successfully done."
			emailext(
subject: "[Jenkins] FAILED!",
body: """
Job: ${env.JOB_NAME}
URL: ${env.BUILD_URL}
Failed stage: "${STAGE}"
""",
        to: 'andrewronin1989@gmail.com',
        from: 'andrewronin1989@gmail.com'
)
		}
}
}

// node ('Host-Node') { 

// STAGE = 'Apavarnitsyn-Checkout';
// 	stage ("${STAGE}") {
//     	git branch: "apavarnitsyn", url: 'https://github.com/MNT-Lab/p192e-module.git'
// 	}

// STAGE = 'Apavarnitsyn-Deployment';
// 	stage ("${STAGE}") {
// sh """
// cd files/
// ./script.sh ${env.BUILD_NUMBER}
// """
// 	}
// }


