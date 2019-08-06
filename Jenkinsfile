node('Host-Node') {
    stage('Checkout GitHub Repository') {
        git branch: 'abutsko',
            url: 'https://github.com/MNT-Lab/p192e-module.git'
    }

    stage('Build Project') {
        withMaven(
            maven: 'Maven 3.6.1',
            mavenSettingsConfig: 'Maven2-Nexus-Repos'
        ) {
            sh 'mvn -f helloworld-ws/pom.xml package'
        }
    }

    stage('Sonar Scanning') {
        def scannerHome = tool 'SonarQubeScanner';
        withSonarQubeEnv() {
            sh "${scannerHome}/bin/sonar-scanner \
               -Dsonar.projectName=abutsko-helloworld \
               -Dsonar.projectKey=abutsko-helloworld \
               -Dsonar.language=java \
               -Dsonar.sources=helloworld-ws/src \
               -Dsonar.java.binaries=**/target/classes"
        }
    }

    stage('Integration Tests') {
        withMaven(
            maven: 'Maven 3.6.1',
            mavenSettingsConfig: 'Maven2-Nexus-Repos'
        ) {
            steps {
                parallel (
                    'Pre-Integration Test': {
                        sh 'mvn -f /helloworld-ws/pom.xml pre-integration-test'
                    },
                    'Integration Test': {
                        sh 'mvn -f /helloworld-ws/pom.xml integration-test '
                    },
                    'Post-Integration Test': {
                        sh 'mvn -f /helloworld-ws/pom.xml post-integration-test'
                    }
                )
            }
        }
    }
}
