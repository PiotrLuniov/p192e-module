node {
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
        copyArtifacts(projectName: 'MNT-LAB-hbledai-child-1-build-job')
    }   sh 'tar xzvf hbledai_dsl_script.tar.gz && ls' 
}