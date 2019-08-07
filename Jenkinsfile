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
    stage('run-parallel-branches') {
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
                def customImage = docker.build("hbledai:${env.BUILD_ID}", "-f ${dockerfile} .")

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
            def CONTAINER_NAME = "hbledai:${env.BUILD_ID}"
podTemplate(cloud: 'Kubernetes')
{
  node('HBLEDAI_kubectl'){
    stage ('test'){
    sh '''   
    if [ ! $(kubectl get secret -n hbledai | grep -q regcred && echo $?) ]
    then
    kubectl create secret docker-registry regcred --docker-server=nexus-ci.playpit.by:6566 --docker-username=admin --docker-password=admin123
    fi
    '''
    
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

