
/*
node ('Host-Node') { 

	stage ('Apavarnitsyn-Checkout') {
    git branch: "apavarnitsyn", url: 'https://github.com/MNT-Lab/p192e-module.git'

	}

	stage ('Apavarnitsyn-Maven-Build') {
 			
sh """ 

cat << EOF > helloworld-ws/src/main/webapp/test.html
 <!DOCTYPE html>
<html>
<body>
<h3>Healt page</h3>
<p>version 1.0.147</p>
<p>Author: Andrey Pavarnitsyn</p>
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


  stage('Sonar-scan') {
    def scannerHome = tool 'SonarQubeScanner';
    withSonarQubeEnv() {
       sh "${scannerHome}/bin/sonar-scanner " +
       '-Dsonar.projectKey=apavarnitsyn ' +
       '-Dsonar.language=java ' +
       '-Dsonar.sources=helloworld-ws/src/main/java  '+
       '-Dsonar.java.binaries=**\/target/classes'
    }
  } 

  stage('run-parallel-mvn-tests') {
    parallel(
	    a: {
	    withMaven(
	        jdk: 'JDK9',
	        maven: 'Maven 3.6.1', 
	        mavenSettingsConfig: 'Maven2-Nexus-Repos') {
	    	sh "mvn pre-integration-test -f helloworld-ws/pom.xml"
	    	}
	    },
	    b: {
	    withMaven(
	        jdk: 'JDK9',
	        maven: 'Maven 3.6.1', 
	        mavenSettingsConfig: 'Maven2-Nexus-Repos') {	
	        sh "mvn integration-test -f helloworld-ws/pom.xml"
	    	}
	    },
	    c: {
	    withMaven(
	        jdk: 'JDK9',
	        maven: 'Maven 3.6.1', 
	        mavenSettingsConfig: 'Maven2-Nexus-Repos') {
	        sh "mvn post-integration-test -f helloworld-ws/pom.xml"
	        }
		}
    )
        
    }



  stage('Triggering job'){
    build job: 'MNTLAB-apavarnitsyn-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'apavarnitsyn')], wait: true
}
  stage('Packaging and Publishing results'){
    copyArtifacts(projectName: 'MNTLAB-apavarnitsyn-child1-build-job')
    sh 'tar xzvf apavarnitsyn_dsl_script.tar.gz && ls'
} 
  stage('Build-Docker-image') {



  	stage('Packaging and Publishing results'){
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
  }
}
*/
node ('k8s-slave') { 
	stage ('Apavarnitsyn-Checkout') {
    git branch: "apavarnitsyn", url: 'https://github.com/MNT-Lab/p192e-module.git'

	}

  stage ('Apavarnitsyn-CD-Kubectl - Build') {
 	
sh """ 

function kubeswitch {
echo "kubectl switch from \$1 to \$2"
    echo "\$2 install"
    kubectl apply -f files/\$2/deployment.yml --namespace=apavarnitsyn
    kubectl apply -f files/\$2/service.yml --namespace=apavarnitsyn
    echo "sleep"
    sleep 30
    TEST_CURL=\$(curl -IL tomcat-\$2-svc.apavarnitsyn.svc.k8s.playpit.by:8080/hello/)

    if [ \$(echo "$TEST_CURL" | grep -c 'HTTP/1.1 200') -gt 0 ]

        
        then
            echo "heath-page checked"
            kubectl apply -f files/\$2/ingress.yml --namespace=apavarnitsyn
            echo "Everything is OK. Clean up"
            kubectl delete -f files/\$1/deployment.yml --namespace=apavarnitsyn
            kubectl delete -f files/\$1/service.yml  --namespace=apavarnitsyn
            echo "Tomcat \$2 installed succesfully"    
        else
            echo "ALARM! Traceback!"
            kubectl delete -f files/\$2/deployment.yml --namespace=apavarnitsyn
            kubectl delete -f files/\$2/service.yml --namespace=apavarnitsyn
    fi


}


# main


TEST=\$(kubectl get pods) 
if [ \$(echo "$TEST" | grep -c 'tomcat-blue') -lt 1 ]
	then 
    kubeswitch green blue 
else
	kubeswitch blue green
fi 
""" 
	}
}
