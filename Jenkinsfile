@Library('hbledai-share-libs') _

node ('Host-Node'){
    stage('preparation') {
        git branch: 'hbledai', url: 'https://github.com/MNT-Lab/p192e-module.git'
    }
    stage('Building code'){
        withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
            sh 'mvn clean package -f helloworld-ws/pom.xml '
        }
    }
    stage('Sonar'){
        def scannerHome = tool 'SonarQubeScanner';
    withSonarQubeEnv() { // If you have configured more than one global server connection, you can specify its name
      sh "${scannerHome}/bin/sonar-scanner " +
          '-Dsonar.projectKey=helloworld-ws:hbledai ' +
          '-Dsonar.sources=helloworld-ws/src/main/java ' +
          '-Dsonar.java.binaries=**/target/classes ' +
          '-Dsonar.language=java '
            
        }
    }
 /*   stage('run-parallel-branches') {
        parallel(
            'pre-integration-test': {
                withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
                sh 'mvn pre-integration-test -f helloworld-ws/pom.xml '
                }
            },
            'integration-test': {
                withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
                sh 'mvn integration-test -f helloworld-ws/pom.xml '
                    }
            },
            'post-integration-test': {
                withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
                sh 'mvn post-integration-test -f helloworld-ws/pom.xml '
                    }
                }
            )
        }*/
    stage('Triggering job and fetching artefact after finishing'){
        build job: 'MNT-LAB-hbledai-child-1-build-job', parameters: [string(name: 'BRANCH', value: 'hbledai')], wait: true
    }

    
    stage('Packaging and Publishing results'){
         parallel(
            'Archiving artifact':{
                copyArtifacts(projectName: 'MNT-LAB-hbledai-child-1-build-job')
        sh '''
        tar xzvf hbledai_dsl_script.tar.gz
        ls helloworld-ws/target/ 
        tar czvf pipeline-hbledai-${BUILD_NUMBER}.tar.gz Jenkinsfile output.txt helloworld-ws/target/helloworld-ws.war
            '''
               /* def pom =readMavenPom file: 'helloworld-ws/pom.xml'
                nexusPublisher nexusInstanceId: 'nexus', 
                    nexusRepositoryId: 'MNT-pipeline-training', 
                    packages: [
                        [
                            $class: 'MavenPackage', 
                            mavenAssetList: [
                                [
                                    filePath: "pipeline-hbledai-${env.BUILD_NUMBER}.tar.gz"
                                    ]
                                    ], 
                    mavenCoordinate: [
                        artifactId: "hbledai", 
                        groupId: 'pipeline', 
                        packaging: '.tar.gz', 
                        version: '${env.BUILD_NUMBER}']
                        ]
                        ]
*/},
            'Creating Docker Image':{
                       sh '''
cat << EOF > $WORKSPACE/Dockerfile
# Pull base image
From tomcat:8-jre8

# Maintainer
MAINTAINER "bledai"

# Copy to images tomcat path
ADD helloworld-ws/target/helloworld-ws.war  /usr/local/tomcat/webapps/
EOF
'''


                docker.withRegistry('http://localhost:6566', 'nexus') {
                def dockerfile = 'Dockerfile'
                def customImage = docker.build("helloworld-hbledai:${env.BUILD_ID}", "-f ${dockerfile} .")

                /* Push the container to the custom Registry */
                customImage.push()
                }
            }
            )
        }
stage("Asking for manual approval") {
                timeout(time: 300, unit: 'SECONDS') {                  
                       def INPUT_PARAMS = input message: 'Please Provide Parameters', ok: 'Next'  
                }
            }
            def CONTAINER_NAME = "helloworld-hbledai:${env.BUILD_ID}"

podTemplate(cloud: 'k8s_bledai', 
            containers: [
                containerTemplate(
                  image: 'hbledai/jenkins-slave:ansible-kubectl', 
                    name: 'jnlp', 
                    ttyEnabled: true
                    )], 
                label: 'K8S_HBLEDAI', 
                name: 'jenkins-slave', 
                namespace: 'hbledai', )
{
    node ('K8S_HBLEDAI'){
    def k8s = new K8s()
    stage ('test'){
     k8s.kubectl_apply (k8s.deployFile(container_name: CONTAINER_NAME,
        creds: 'dockerrepo', 
        file_name: 'deploy_tomcat.yml', 
        app_name: 'helloworld-ws', 
        container_port: '8080'))
    /*sh """   
    cat << EOF > hello.yaml
apiVersion: extensions/v1beta1 
kind: Deployment
metadata:
  name: tomcat
  labels:
    app: tomcat
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  replicas: 1
  selector:
    matchLabels:
      app: tomcat
  template:
    metadata:
      labels:
        app: tomcat
    spec:
      containers:
      - name: tomcat
        image: registry-ci.playpit.by/${CONTAINER_NAME}
        ports:
        - containerPort: 8080
        readinessProbe:
          httpGet:
            path: /helloworld-ws
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
          successThreshold: 1        
      imagePullSecrets:
      - name: dockerrepo

---
apiVersion: v1
kind: Service
metadata:
  name: tomcat-svc
spec:
  type: LoadBalancer
  ports:
    - port: 8080
      targetPort: 8080
      name: tomcat-svc-p
  selector:
    app: tomcat
---
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: tomcat-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /

spec:
  rules:
  - host: hbledai.k8s.playpit.by
    http:
      paths:
      - path: /
        backend:
          serviceName: tomcat-svc
          servicePort: 8080
EOF
kubectl apply -f hello.yaml
    """*/

        }       
    }
}
  emailext (
      subject: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """
STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':
Check console output at "${env.JOB_NAME} [${env.BUILD_NUMBER}]"
""",
      recipientProviders: [brokenBuildSuspects()],
      to: 'hannabledai@gmail.com'
    )  
}

