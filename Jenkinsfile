
node('Host-Node'){
	stage('Preparation (Checking out)'){
		git branch: "kkaminski", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}
}

// node{
//     stage('Checking out'){
//        git 'https://github.com/MNT-Lab/p192e-module'
//     }
//     stage('Building code'){
//        withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
//        sh 'mvn clean package -f helloworls-ws/pom.xml' 
//        }
//     }
//}

//     stage('Sonar scan') {
//         sh 'make publish'
//     }

//     stage('Testing') {
//         sh 'make publish'
//     }
 
//     stage('Triggering job and fetching artefact after finishing') {
//         sh 'make publish'
//     }

//     stage('Packaging and Publishing results') {
//         sh 'make publish'
//     }

//     stage('Asking for manual approval') {
//         sh 'make publish'
//     }

//     stage('Deployment') {
//         sh 'make publish'
//     }
// } 

