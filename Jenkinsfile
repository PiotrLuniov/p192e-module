node('Host-Node') {
    stage('Checkout GitHub Repository') {
        git branch: 'abutsko',
            url: 'https://github.com/MNT-Lab/p192e-module.git'
    }

    stage('Build Project') {
        // Create help page for application
        writeFile file: 'helloworld-ws/src/main/help.html',
                  text: '''
Developer: Antoś Bućko
Image: ${env.BUILD_NUMBER}
Git Information:
\$(git log | head -n 3)
        '''

        withMaven(
            maven: 'Maven 3.6.1',
            mavenSettingsConfig: 'Maven2-Nexus-Repos'
        ) {
            sh 'mvn -f helloworld-ws/pom.xml package'
        }
    }
    
    //stage('Sonar Scanning') {
    //    def scannerHome = tool 'SonarQubeScanner';
    //    withSonarQubeEnv() {
    //        sh "${scannerHome}/bin/sonar-scanner \
    //           -Dsonar.projectName=abutsko-helloworld \
    //           -Dsonar.projectKey=abutsko-helloworld \
    //           -Dsonar.language=java \
    //           -Dsonar.sources=helloworld-ws/src \
    //           -Dsonar.java.binaries=**/target/classes"
    //    }
    //}

    //stage('Integration Tests') {
    //    withMaven(
    //        maven: 'Maven 3.6.1',
    //        mavenSettingsConfig: 'Maven2-Nexus-Repos'
    //    ) {
    //        parallel (
    //            'Pre-Integration Test': {
    //                sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
    //            },
    //            'Integration Test': {
    //                sh 'mvn -f helloworld-ws/pom.xml integration-test'
    //            },
    //            'Post-Integration Test': {
    //                sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
    //            }
    //        )
    //    }
    //}
    
    def triggeredJob = 'MNTLAB-abutsko-child1-build-job'
    stage("Trigger ${triggeredJob}") {
        build job: "${triggeredJob}",
              parameters: [
                string(
                    name: 'BRANCH_NAME',
                    value: 'abutsko'
                )
              ],
              wait: true

        copyArtifacts projectName: "${triggeredJob}",
                      filter: 'output.txt'
    }

    def archive = "pipeline-abutsko-${env.BUILD_NUMBER}.tar.gz"
    stage('Packaging and Publishing results') {
        parallel(
            'Create Archive for common files And Upload them': {
                sh "tar czf ${archive} --transform='flags=r;s!^.*/!!' output.txt Jenkinsfile **/**/helloworld-ws.war"

                def pom =readMavenPom file: 'helloworld-ws/pom.xml'
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: 'localhost:8081',
                    repository 'MNT-pipeline-training',
                    groupId: pom.groupId,
                    version: pom.version,
                    credentialsId: 'nexus',
                    artifacts: [
                        artifactId: pom.artifactId,
                        classifier: '',
                        file: archive,
                        type: 'tar.gz'
                    ]
                )
            },
            'Building And Pushing Docker Image': {
                // Create Dockerfile
                writeFile file: 'Dockerfile',
                          text: '''
FROM tomcat:8.0
COPY helloworld-ws/target/helloworld-ws.war /usr/local/tomcat/webapps/
                '''

                docker.withRegistry('http://localhost:6566', 'nexus') {
                    def appImage = docker.build("localhost:6566/helloworld-abutsko:${env.BUILD_NUMBER}")
                    appImage.push()
                }
            }
        )
    }
}
