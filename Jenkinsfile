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
                def scannerHome = tool 'SonarQubeScanner';
                withSonarQubeEnv() {
			sh "${scannerHome}/bin/sonar-scanner " +
          		'-Dsonar.projectKey=hkanonik ' +
			'-Dsonar.projectName=hkanonik ' +
          		'-Dsonar.sources=helloworld-ws/src/main/java ' +
          		'-Dsonar.java.binaries=**/target/classes ' +
          		'-Dsonar.language=java '
            			
			//sh '''
                        //${scannerHome}/bin/sonar-scanner \
                        //-Dsonar.projectKey= \
                        //-Dsonar.projectName=hkanonik \
                        //-Dsonar.sources=helloworld-ws/src/main/java \
                        //-Dsonar.java.binaries=**/target/classes \
                        //-Dsonar.language=java
                        //'''
			
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
