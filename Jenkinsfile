node('Host-Node') {
	stage("1: Git") {
		git branch: 'hkanonik', url: 'https://github.com/MNT-Lab/p192e-module.git'
        }
	
	stage('2: Build') {
		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
		    sh: 'mvn clean package -f helloworld-ws/pom.xml'
	    }
	}
	stage('3: SonarQube') {
		withSonarQubeEnv(credentialsId: '23e830fe-cdbd-4ec3-b1de-bd3bef64947f') {

        	}
    	}
	stage('4: Testing') {
		parallel(
			'Pre Integration': {
				withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                    			sh '''
                    			cd helloworld-project/helloworld-ws/ 
                    			mvn pre-integration-test
                    			'''
                		}
            		},
            		'Integration': {
                		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                    			sh '''
                    			cd helloworld-project/helloworld-ws/
                    			mvn integration-test
                    			'''
                		}
            		},
            		'Post Integration': {
                		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
                    			sh '''
                    			cd helloworld-project/helloworld-ws/
                    			mvn post-integration-test
                    			'''
                		}
            		}
		)
	}
}
