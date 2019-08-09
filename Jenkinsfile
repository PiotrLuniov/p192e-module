@Library('hbledai-share-libs') _
def CONTAINER_NAME = "helloworld-hbledai:${env.BUILD_ID}"
node ('Host-Node'){
    stage('preparation') {
      try {
        git branch: 'hbledai', url: 'https://github.com/MNT-Lab/p192e-module.git'
        sh """
        sed -i "s/_image_/${CONTAINER_NAME}/" helloworld-ws/src/main/webapp/healthz.html
        """}
        catch (err) {
            echo err.getMessage()
            echo "Error detected, but we will continue."
        }
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
    stage('run-parallel-branches') {
      try{
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
      }
       catch (err) {
            echo err.getMessage()
            echo "Error detected, but we will continue."
        }
        }
    stage('Triggering job and fetching artefact after finishing'){
        build job: 'MNT-LAB-hbledai-child-1-build-job', parameters: [string(name: 'BRANCH', value: 'hbledai')], wait: true
    }

    
    stage('Packaging and Publishing results'){
       
         parallel(
             
            'Archiving artifact':{
                try{ 
                copyArtifacts(projectName: 'MNT-LAB-hbledai-child-1-build-job')
        sh '''
        tar xzvf hbledai_dsl_script.tar.gz
        ls helloworld-ws/target/ 
        tar czvf pipeline-hbledai-${BUILD_NUMBER}.tar.gz Jenkinsfile output.txt helloworld-ws/target/helloworld-ws.war
            '''
           
                 nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: 'localhost:8081',
                        repository: 'MNT-pipeline-training',
                        groupId: 'hbledai',
                        version: "${env.BUILD_NUMBER}",
                        credentialsId: 'nexus',
                        artifacts: [
                            [
                                artifactId: 'hbledai',
                                classifier: '',
                                file: "pipeline-hbledai-${BUILD_NUMBER}.tar.gz",
                                type: 'tar.gz'
                            ]
                        ]
                    )
                } catch (err) {
            echo err.getMessage()
            echo "Error detected, but we will continue."
        }
                
    
},
            'Creating Docker Image': {
                try{
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
                    catch (err) {
            echo err.getMessage()
            echo "Error detected, but we will continue."
                   }
            }
      )
}
stage("Asking for manual approval") {
                timeout(time: 300, unit: 'SECONDS') {                  
                       def INPUT_PARAMS = input message: 'Please Provide Parameters', ok: 'Next'  
                }
            }
            

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
    stage ('Deployment (rolling update, zero downtime)'){
      k8s.kubectl_apply (
        k8s.deployFile( CONTAINER_NAME,  'dockerrepo',  'deploy_tomcat.yml',  'helloworld-ws', 
 '8080'
          )
        )

      k8s.kubectl_apply (
        k8s.serviceFile(
          'service_tomcat.yaml', 'tomcat-svc', '8080', '8080'
          )
        )
      k8s.kubectl_apply (
        k8s.ingressFile(
          'ingress_tomcat.yaml', 'tomcat-ingress', 'tomcat-svc', '8080'
          )
        )
   

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

