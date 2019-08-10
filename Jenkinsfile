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
        withMaven(
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


//    stage('Testing') {
//        parallel 'pre-integration-test': {
//                withMaven(globalMavenSettingsConfig: "$MV_CONF", jdk: 'JDK9', maven: "$MV_V") {
//                sh 'mvn pre-integration-test -f helloworld-ws/pom.xml'
//            }
//        },
//                'integration-test': {
//                    withMaven(globalMavenSettingsConfig: "$MV_CONF", jdk: 'JDK9', maven: "$MV_V") {
//                        sh 'mvn integration-test -f helloworld-ws/pom.xml'
//                    }
//                },
//                'post-integration-test': {
//                    withMaven(globalMavenSettingsConfig: "$MV_CONF", jdk: 'JDK9', maven: "$MV_V") {
//                        sh 'mvn post-integration-test -f helloworld-ws/pom.xml'
//                    }
//                }
//
//    }


    stage('Triggering job and fetching artefact after finishing'){
        build job: "MNTLAB-${STUDENT}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "${STUDENT}")], wait: true
        copyArtifacts filter: "output.txt", fingerprintArtifacts: true, projectName: "MNTLAB-${STUDENT}-child1-build-job", selector: lastSuccessful()
    }



    stage('Packaging and Publishing results') {
        parallel(
                'Archiving artifact': {
                    copyArtifacts filter: "output.txt", fingerprintArtifacts: true, projectName: "MNTLAB-${STUDENT}-child1-build-job", selector: lastSuccessful()
                    sh "tar cvzf pipeline-${STUDENT}-${BUILD_NUMBER}.tar.gz output.txt Jenkinsfile helloworld-ws/target/helloworld-ws.war"
                    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: "pipeline-${STUDENT}-\${BUILD_NUMBER}.tar.gz"]], mavenCoordinate: [artifactId: "${STUDENT}", groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]]
                },
                'Creating Docker Image': {
                    withDockerRegistry(credentialsId: 'nexus', url: 'http://localhost:6566') {
                        sh "docker build -t localhost:6566/helloworld-${STUDENT}:${BUILD_NUMBER} -f Dockerfile ."
                        sh "docker push localhost:6566/helloworld-${STUDENT}:${BUILD_NUMBER}"
                    }

                }
        )

    }

   stage('Asking for manual approval'){
    timeout(time: 5, unit: 'MINUTES') {
        input(id: "Deploying artifacts", \
              message: "Is it necessary to deploy helloworld-${STUDENT}:${env.BUILD_NUMBER}?", ok: "yes")
    }
}

	stage('Deployment'){
		
	echo 'creation ns'
		sh "$HOME/kubectl create namespace ${STUDENT}"
//        sh'''
//        sed -i "s/STUD/${STUDENT}/g" ns.yaml
//        $HOME/kubectl apply -f ns.yaml  
//            '''
//		echo "downloading config files"
//        git branch: "STUDENT",
//                url: 'https://github.com/MNT-Lab/p192e-module.git'
        sh'''
        ls
	pwd
	sed -i "s/_BUILD_NUMBER/${BUILD_NUMBER}/g" deployment.yaml
        $HOME/kubectl apply -f deployment.yaml        
        '''
	}



}
