node('Host-Node') {
    def student = "iyaruk"
    stage('Checkout GitHub Repository') {
        git branch: '${student}',
            url: 'https://github.com/MNT-Lab/p192e-module.git'
    }

    stage('Build Project') {
         withMaven(maven: 'Maven 3.6.1') {
            sh 'mvn -f helloworld-ws/pom.xml package'
        }
    }
    stage('Sonar Scan') {
    def scannerHome = tool 'SonarQubeScanner';
    withSonarQubeEnv() {
    sh "${scannerHome}/bin/sonar-scanner \
               -Dsonar.projectName=${student}-helloworld \
               -Dsonar.projectKey=${student}-helloworld \
               -Dsonar.language=java \
               -Dsonar.sources=helloworld-ws/src \
               -Dsonar.java.binaries=**/target/classes"
        }
    }
    
    stage('Tests') {
        withMaven(maven: 'Maven 3.6.1',) {
            parallel (
                '1 - Pre-Int': {
                    sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                   },
                '2 - Int': {
                    sh 'mvn -f helloworld-ws/pom.xml integration-test'
                   },
                '3 - Post-Int': {
                    sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
                   }
            
            )
        }
     stage('Triggering and fetching'){
        build job: 'MNT-LAB-iyaruk-child-1-build-job', parameters: [string(name: 'BRANCH', value: '${student}')], wait: true
    }
    }
}
