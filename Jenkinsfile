node {
	stage('Preparation') {
		checkout([$class: 'GitSCM', branches: [[name: '*/mmarkova']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p192e-module']]])
	}
	stage('Building code') {
		git url: 'https://github.com/MNT-Lab/p192e-module'
 
    	withMaven(
        maven: 'Maven 3.6.1', 
        globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac') { 
      		sh "mvn -f helloworld-ws/pom.xml package"
		}
	}
	stage('Sonar scan') {
		 withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac', installationName: 'SonarQubeScanner') { 
      		sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.3.0.1492:sonar'
      	}
	}
	stage('Testing') {
        parallel {
            stage('pre-integration test') {
            	git url: 'https://github.com/MNT-Lab/p192e-module'
 
		    	withMaven(
		        maven: 'Maven 3.6.1',
		        mavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac') {
		      		sh "mvn -f helloworld-ws/pom.xml pre-integration-test"
				}
            } 
            stage('integration test') {
            	git url: 'https://github.com/MNT-Lab/p192e-module'
 
		    	withMaven(
		        maven: 'Maven 3.6.1',
		        mavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac') {
		      		sh "mvn -f helloworld-ws/pom.xml integration-test"
				}
            }
            stage('post-integration test') {
            	git url: 'https://github.com/MNT-Lab/p192e-module'
 
		    	withMaven(
		        maven: 'Maven 3.6.1',
		        mavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac') {
		      		sh "mvn -f helloworld-ws/pom.xml post-integration-test"
				}
            }
        }
    }
    // stage('Triggering job and fetching artefact after finishing') {

    // }
 //    stage('Packaging and Publishing results') {

 //    }
 //    stage('Asking for manual approval') {

 //    }
 //    stage('Deployment (rolling update, zero downtime') {

 //    }
}
