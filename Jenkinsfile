@Library('abutsko-library') _

def sendEmail(String stage) {
    emailext(
        subject: "[Jenkins] FAILED!",
        body: """
Job: ${env.JOB_NAME}
URL: ${env.BUILD_URL}
Failed stage: ${stage}
        """,
        to: 'anton.butsko@gmail.com',
        from: 'anton.butsko@gmail.com'
    )
}

node('Host-Node') {
    stage('Build Project') {
        // Add git information to help page
        // I want to cute output in web-browser
        try {
            sh 'echo "<head>\\n<meta charset="UTF-8">\\n</head>" > helloworld-ws/src/main/webapp/index.html'
            sh 'echo "<pre>" >> helloworld-ws/src/main/webapp/index.html'
            sh "echo 'Image: ${env.BUILD_NUMBER}' >> helloworld-ws/src/main/webapp/index.html"
            sh 'git log | head -n 3 >> helloworld-ws/src/main/webapp/index.html'
            sh 'echo "</pre>" >> helloworld-ws/src/main/webapp/index.html'

            withMaven(
                maven: 'Maven 3.6.1',
                mavenSettingsConfig: 'Maven2-Nexus-Repos'
            ) {
                sh 'mvn -f helloworld-ws/pom.xml package'
            }
        } catch(all) {
            sendEmail('Build Project')
        }
    }
    
    stage('Sonar Scanning') {
        try {
            def scannerHome = tool 'SonarQubeScanner';
            withSonarQubeEnv() {
                sh "${scannerHome}/bin/sonar-scanner \
                   -Dsonar.projectName=abutsko-helloworld \
                   -Dsonar.projectKey=abutsko-helloworld \
                   -Dsonar.language=java \
                   -Dsonar.sources=helloworld-ws/src \
                   -Dsonar.java.binaries=**/target/classes"
            }
        } catch(all) {
            sendEmail('Sonar Scanning')
        }
    }

    stage('Integration Tests') {
        try {
            withMaven(
                maven: 'Maven 3.6.1',
                mavenSettingsConfig: 'Maven2-Nexus-Repos'
            ) {
                parallel (
                    'Pre-Integration Test': {
                        echo 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                    },
                    'Integration Test': {
                        echo 'mvn -f helloworld-ws/pom.xml integration-test'
                    },
                    'Post-Integration Test': {
                        echo 'mvn -f helloworld-ws/pom.xml post-integration-test'
                    }
                )
            }
        } catch(all) {
            sendEmail('Integration Tests')
        }
    }
    
    def triggeredJob = 'MNTLAB-abutsko-child1-build-job'
    stage("Trigger ${triggeredJob}") {
        try {
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
        } catch(all) {
            sendEmail("Trigger ${triggeredJob}")
        }
    }

    def archive = "pipeline-abutsko-${env.BUILD_NUMBER}"
    stage('Packaging and Publishing results') {
        try {
            parallel(
                'Create Archive for common files And Upload them': {
                    sh "tar czf ${archive}.tar.gz --transform='flags=r;s!^.*/!!' output.txt Jenkinsfile **/**/helloworld-ws.war"

                    def pom =readMavenPom file: 'helloworld-ws/pom.xml'
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: 'localhost:8081',
                        repository: 'MNT-pipeline-training',
                        groupId: 'pipeline',
                        version: "${env.BUILD_NUMBER}",
                        credentialsId: 'nexus',
                        artifacts: [
                            [
                                artifactId: 'abutsko',
                                classifier: '',
                                file: "${archive}.tar.gz",
                                type: 'tar.gz'
                            ]
                        ]
                    )
                },
                'Building And Pushing Docker Image': {
                    docker.withRegistry('http://registry-ci.playpit.by', 'nexus') {
                        def appImage = docker.build("registry-ci.playpit.by/helloworld-abutsko:${env.BUILD_NUMBER}", '-f config/Dockerfile .')
                        appImage.push()
                    }
                }
            )
        } catch(all) {
            sendEmail('Packaging and Publishing results')
        }
    }

    stage('Asking for manual approval') {
        try {
            timeout(time: 5, unit: 'MINUTES') {
                input(
                    id: 'Deployment',
                    message: 'Do you want to deploy Docker image?',
                    ok: 'Deploy'
                )
            }
        } catch(all) {
            sendEmail('Asking for manual approval')
        }
     }
}

stage('Quality Gate') {
    try {
        timeout(time: 1, unit: 'HOURS') {
            def qualityGate = waitForQualityGate()

            if (qualityGate.status != 'OK') {
                error "Pipeline aborted due to quality gate failure: ${qualityGate.status}"
            }
        }
    } catch(all) {
        sendEmail('Quality Gate')
    }
}

podTemplate(
    name: 'abutsko',
    cloud: 'Kubernetes',
    containers: [
        containerTemplate(
            name: 'jnlp',
            image: 'dranser/jenkins-jnlp-kubectl-ansible',
            ttyEnabled: true
        )
    ],
    serviceAccount: 'jenkins',
    namespace: 'abutsko'
) {
    node(POD_LABEL) {
//        stage('Download files of configuration') {
//            try {
//                git branch: 'abutsko',
//                    url: 'https://github.com/MNT-Lab/p192e-module.git'
//            } catch(all) {
//                sendEmail('Download files of configuration')
//            }
//        }

        stage('Deploying a new application version') {
            try {
                // set git hash for sanity check
                def gitHash = sh(returnStdout: true,
                                 script: 'git log -n 1 --pretty=format:"%H"'.trim()
                              )
                sh "sed -i 's/PLACE_FOR_GIT_HASH/${gitHash}/' config/provision.yml"

                // set a new version for image
                sh "sed -i 's/PLACE_FOR_NEW_TAG/${env.BUILD_NUMBER}/' config/sanity.yml"
                sh "sed -i 's/PLACE_FOR_NEW_TAG/${env.BUILD_NUMBER}/' config/deployment.yml"

                ansiblePlaybook(
                    playbook: 'config/provision.yml',
                    extras: '-v'
                )
            } catch(all) {
                sendEmail('Deploying a new application version')
            }
        }
    }
}

//node('Host-Node') {
//    emailext(
//        subject: "[Jenkins] SUCCESS!",
//        body: """
//Job: ${env.JOB_NAME}
//URL: ${env.BUILD_URL}
//        """,
//        to: 'anton.butsko@gmail.com',
//        from: 'anton.butsko@gmail.com'
//    )
//}
