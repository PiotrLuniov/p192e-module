node {
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
}
