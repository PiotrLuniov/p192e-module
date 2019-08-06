
node ('Host-Node') { 

	stage ('Apavarnitsyn-Checkout') {
    git branch: "apavarnitsyn", url: 'https://github.com/MNT-Lab/p192e-module.git'

	}

	stage ('Apavarnitsyn-Maven-Build') {
 			// Shell build test.html step
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
 """		// Maven build step
	withMaven(
    jdk: 'JDK9',
    maven: 'Maven 3.6.1', 
    mavenSettingsConfig: 'Maven2-Nexus-Repos') { 

 		sh "mvn -f helloworld-ws/pom.xml clean package" 

 		}	
  }	

  stage('Sonar-scan') {
    def scannerHome = tool 'SonarQubeScanner';
    withSonarQubeEnv() {
       sh "${scannerHome}/bin/sonar-scanner " +
       '-Dsonar.projectKey=apavarnitsyn ' +
       '-Dsonar.language=java ' +
       '-Dsonar.sources=helloworld-ws/src/main/java  '+
       '-Dsonar.java.binaries=**/target/classes'
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
  stage('build-Docker-image') {
    // Shell docker build step
sh """ 
cp helloworld-project/helloworld-ws/target/helloworld-ws.war hello.war
cat << EOF > Dockerfile
FROM tomcat
COPY hello.war /usr/local/tomcat/webapps/ 
EOF

docker build -t docker/helloworld-apavarnitsyn:$BUILD_NUMBER .
docker login -u admin -p admin123 http://nexus-ci.playpit.by:8082/v1/
docker tag docker/helloworld-apavarnitsyn:$BUILD_NUMBER nexus-ci.playpit.by:8082/helloworld-apavarnitsyn:$BUILD_NUMBER
docker push nexus-ci.playpit.by:8082/helloworld-apavarnitsyn:$BUILD_NUMBER


""" 
}
}

node () { 

  stage ('Apavarnitsyn-CD-Kubectl - Build') {
 	
sh """ 

function kubeswitch {
echo "kubectl switch from \$1 to \$2"
    echo "\$2 install"
    ./kubectl apply -f \$2/deployment.yml --namespace=apavarnitsyn
    ./kubectl apply -f \$2/service.yml --namespace=apavarnitsyn
    echo "sleep"
    sleep 30
    TEST_CURL=\$(curl -IL tomcat-\$2-svc.apavarnitsyn.svc.k8s.playpit.by:8080/hello/)
    TEST_CURL1=\$(curl  tomcat-\$2-svc.apavarnitsyn.svc.k8s.playpit.by:8080/hello/test.html)

# if [ \$(echo "$TEST_CURL1" | grep -c 'version') -gt 0 ]

    if [ \$(echo "$TEST_CURL" | grep -c 'HTTP/1.1 200') -gt 0 ]

        
        then
            echo "heath-page checked"
            ./kubectl apply -f \$2/ingress.yml --namespace=apavarnitsyn
            echo "Everything is OK. Clean up"
            ./kubectl delete -f \$1/deployment.yml --namespace=apavarnitsyn
            ./kubectl delete -f \$1/service.yml  --namespace=apavarnitsyn
            echo "Tomcat \$2 installed succesfully"    
        else
            echo "ALARM! Traceback!"
            ./kubectl delete -f \$2/deployment.yml --namespace=apavarnitsyn
            ./kubectl delete -f \$2/service.yml --namespace=apavarnitsyn
    fi


}

function deploycreate {

cat << EOF > \$1/deployment.yml
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: tomcat-\$1
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: tomcat-\$1
    spec:
      containers:
        - name: tomcat-\$1
          image: nexus-ci.playpit.by:8082/helloworld-apavarnitsyn:$BUILD_NUMBER
          ports:
            - containerPort: 8080
      imagePullSecrets:
        - name: regcred
EOF
cat << EOF > \$1/service.yml
apiVersion: v1
kind: Service
metadata:
  labels:
    app: tomcat-\$1
  name: tomcat-\$1-svc
spec:
  ports:
    - port: 8080
      protocol: TCP
      targetPort: 8080
  selector: 
    app: tomcat-\$1
  type: LoadBalancer
EOF

cat << EOF > \$1/ingress.yml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: tomcat-ingress
spec:
  rules:
  - host: tomcat
    http:
      paths:
      - path: /
        backend:
          serviceName: tomcat-\$1-svc
          servicePort: 8080
EOF

}

# main

curl -LO https://storage.googleapis.com/kubernetes-release/release/\$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
chmod +x ./kubectl 

mkdir green
mkdir blue

deploycreate green
deploycreate blue


TEST=\$(.kubectl get pods) 
if [ \$(echo "$TEST" | grep -c 'tomcat-blue') -lt 1 ]
	then 
    kubeswitch green blue 
else
	kubeswitch blue green
fi 
""" 
	}
}
