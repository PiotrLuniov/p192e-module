
node(){
	stage('Checking out'){
		git branch: "kkaminski", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}
	stage('Building code'){
		withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
			sh 'mvn clean package -f helloworls-ws/pom.xml' 
		}
	}
}
