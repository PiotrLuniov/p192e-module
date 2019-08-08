node {
    def STUDENT = 'ymlechka'
    def MV_CONF = 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac'
    def MV_V = 'Maven 3.6.1'


    stage('Preparation') {

        checkout scm
//        checkout([$class: 'GitSCM', branches: [[name: "*/$STUDENT"]], userRemoteConfigs: [[url: ' https://github.com/MNT-Lab/p192e-module']]])
    }

    stage('Creation metadata page'){
        sh label: '', script: '''builddate=$(date)
        cat << EOF > helloworld-ws/src/main/webapp/metadata.html
        build: $BUILD_NUMBER <br>
        author: ymlechka <br>
        build_url: $BUILD_URL <br>
        buils_data: $builddate
        EOF'''

    }

    stage ('Buildingcode') {
        wit hMaven(
                jdk: 'JDK9', maven: 'Maven 3.6.1') {
            sh 'mvn clean package -f helloworld-ws/pom.xml'
        }
    }
    stage('Sonar scan') {
        def scannerHome = tool 'SonarQubeScanner'
        withSonarQubeEnv('sonar-ci') {
            sh "${scannerHome}/bin/sonar-scanner " +
                    "-Dsonar.projectKey=helloworld-ws-$STUDENT " +
                    '-Dsonar.language=java ' +
                    '-Dsonar.sources=helloworld-ws/src ' +
                    '-Dsonar.java.binaries=helloworld-ws/target'
        }
    }


    stage('Testing') {
        parallel 'pre-integration-test': {
                withMaven(globalMavenSettingsConfig: "$MV_CONF", jdk: 'JDK9', maven: "$MV_V") {
                sh 'mvn pre-integration-test -f helloworld-ws/pom.xml'
            }
        },
                'integration-test': {
                    withMaven(globalMavenSettingsConfig: "$MV_CONF", jdk: 'JDK9', maven: "$MV_V") {
                        sh 'mvn integration-test -f helloworld-ws/pom.xml'
                    }
                },
                'post-integration-test': {
                    withMaven(globalMavenSettingsConfig: "$MV_CONF", jdk: 'JDK9', maven: "$MV_V") {
                        sh 'mvn post-integration-test -f helloworld-ws/pom.xml'
                    }
                }

    }








    }
