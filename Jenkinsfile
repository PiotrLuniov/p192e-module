node('Host-Node') {
    stage('Checkout GitHub Repository') {
        git branch: 'abutsko',
            url: 'https://github.com/MNT-Lab/p192e-module.git'
    }

    stage('Build Project') {
        // Add git information to help page
        sh 'git log | head -n 3 >> config/help.html'

        sh 'cp config/help.html helloworld-ws/src/main/help.html'

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

    def archive = "pipeline-abutsko-${env.BUILD_NUMBER}"
    stage('Packaging and Publishing results') {
        parallel(
            'Create Archive for common files And Upload them': {
                sh "tar czf ${archive}.tar.gz --transform='flags=r;s!^.*/!!' output.txt Jenkinsfile **/**/helloworld-ws.war"

                def pom =readMavenPom file: 'helloworld-ws/pom.xml'
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: 'localhost:8081',
                    repository: 'MNT-pipeline-training',
                    groupId: pom.groupId,
                    version: pom.version,
                    credentialsId: 'nexus',
                    artifacts: [
                        [
                            artifactId: archive,
                            classifier: '',
                            file: "${archive}.tar.gz",
                            type: 'tar.gz'
                        ]
                    ]
                )
            },
            'Building And Pushing Docker Image': {
                docker.withRegistry('http://localhost:6566', 'nexus') {
                    def appImage = docker.build("localhost:6566/helloworld-abutsko:${env.BUILD_NUMBER}", '-f config/Dockerfile .')
                    appImage.push()
                }
            }
        )
    }

    stage('Asking for manual approval') {
        timeout(time: 5, unit: 'MINUTES') {
            input(
                id: 'Deployment',
                message: 'Do you want to deploy Docker image?',
                ok: 'Deploy'
            )
        }
    }

    podTemplate(
        name: 'abutsko',
        cloud: 'Kubernetes',
        containers: [
            containerTemplate(
                name: 'jnlp',
                image: 'dranser/jenkins-jnlp-kubectl',
                ttyEnabled: true
            )
        ],
        serviceAccount: 'jenkins',
        namespace: 'jenkins'
    ) {
        node(POD_LABEL) {
            stage('Check kubectl') {
                sh 'kubectl get pods -A'
            }
        }
    }
}
