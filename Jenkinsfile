node('Host-Node')
{
	
   stage('Preparation') 
{
      git branch: 'kshevchenko', url: 'https://github.com/MNT-Lab/p192e-module.git'
}
  
  stage('Building code')
{
			withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', jdk: 'JDK9', maven: 'Maven 3.6.1') 
{
				sh 'mvn clean package -f helloworld-ws/pom.xml'
stage('Sonar scan'){
			withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1') {
				withSonarQubeEnv(credentialsId: 'c4a2af68-473f-4764-a84f-6520c8bf22ac') {
					sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar -f helloworld-ws/pom.xml -Dsonar.projectKey=kshevchenko -Dsonar.projectName=kshevchenko_helloworld"
				}
}
}
}
}
