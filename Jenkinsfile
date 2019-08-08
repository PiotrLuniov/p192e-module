node('Host-Node') {
    stage('Checkout GitHub Repository') {
        git branch: 'iyaruk',
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
               -Dsonar.projectName=iyaruk-helloworld \
               -Dsonar.projectKey=iyaruk-helloworld \
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
        build job: 'MNTLAB-iyaruk-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'iyaruk')], wait: true
        copyArtifacts filter: 'output.txt', flatten: true, projectName: 'MNTLAB-iyaruk-child1-build-job', selector: workspace()
    }
    
    }
}
