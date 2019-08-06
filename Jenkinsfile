node('Host-Node') {
    stage('Checkout GitHub Repository') {
        git branch: 'abutsko',
            url: 'https://github.com/MNT-Lab/p192e-module.git'
    }

    stage('Build Project') {
        // Create help page for application
        sh  '''
            cat<<EOF > helloworld-ws/src/main/help.html
Developer: Antoś Bućko
Image: ${env.BUILD_NUMBER}
Git Information:
\$(git log | head -n 3)
EOF
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
            'Create Archive for common files': {
                sh "tar czf ${archive} --transform='flags=r;s!^.*/!!' output.txt Jenkinsfile **/**/helloworld-ws.war"
            },
            'Build Docker Image': {
                // Create Dockerfile
                sh '''
                    cat <<EOF > Dockerfile
FROM tomcat:8.0
COPY helloworld-ws/target/helloworld-ws.war /usr/local/tomcat/webapps/
EOF
                '''

                docker.withRegistry('http://localhost:6566', 'nexus') {
                    def appImage = docker.build(
                        "localhost:6566/helloworld-abutsko:${env.BUILD_NUMBER}",
                        "Dockerfile"
                    )
                    appImage.push()
                }
            }
        )
    }
}
