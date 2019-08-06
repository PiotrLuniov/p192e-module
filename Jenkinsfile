node {
	stage('Preparation') {
		checkout([$class: 'GitSCM', branches: [[name: '*/mmarkova']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p192e-module']]])
	}
	stage('Building code') {
		git url: 'https://github.com/MNT-Lab/p192e-module'
 
    	withMaven(
        maven: 'Maven 3.6.1', 
        mavenSettingsConfig: 'MNT-group nexus-ci') { 
      		sh "mvn package"
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
		        mavenSettingsConfig: 'MNT-group nexus-ci') {
		      		sh "mvn pre-integration-test"
				}
            } 
            stage('integration test') {
            	git url: 'https://github.com/MNT-Lab/p192e-module'
 
		    	withMaven(
		        maven: 'Maven 3.6.1',
		        mavenSettingsConfig: 'MNT-group nexus-ci') {
		      		sh "mvn integration-test"
				}
            }
            stage('post-integration test') {
            	git url: 'https://github.com/MNT-Lab/p192e-module'
 
		    	withMaven(
		        maven: 'Maven 3.6.1',
		        mavenSettingsConfig: 'MNT-group nexus-ci') {
		      		sh "mvn post-integration-test"
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
