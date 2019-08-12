node {
/*    stage('clone repo'){
      checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p192e-module.git']]])

   }
  
    stage('Sonar scan') {
       def scannerHome = tool 'SonarQubeScanner'
       withSonarQubeEnv {
          sh "${scannerHome}/bin/sonar-scanner " +
          '-Dsonar.projectKey=helloworld-sbarysevich '+
          '-Dsonar.language=java '+
          '-Dsonar.sources=helloworld-ws/src '+
          '-Dsonar.java.binaries=/helloworld-ws/target'
        }
     }
      
   stage ('Build war') {
        sh label: '', script: '''cat << EOF > helloworld-ws/src/main/webapp/data.html
        build: $BUILD_NUMBER
        <br>author: Barisevich Sergey
        <br>build_url: $BUILD_URL
        <br>buils_data: $(date)
        EOF'''

    	withMaven(globalMavenSettingsConfig: 'e1b3beed-2dd3-45b7-998e-5361dfe1b6ac', maven: 'Maven 3.6.1', mavenSettingsConfig: 'Maven2-Nexus-Repos') {
    	    sh "mvn clean package -f helloworld-ws/pom.xml"
    	}
   }
  
   stage ('Build docker image') {
    	writeFile file: 'Dockerfile', text: '''FROM tomcat
ADD  helloworld-ws/target/helloworld-ws.war /usr/local/tomcat/webapps/'''
     }   

   stage ('Gathering&push artefacts to nexus'){
       sh label: '', script: 'tar -cf pipeline-sbarysevich-$BUILD_NUMBER.tar.gz helloworld-ws/target/helloworld-ws.war'
       nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'pipeline-sbarysevich-$BUILD_NUMBER.tar.gz']], mavenCoordinate: [artifactId: 'sbarysevich', groupId: 'pipeline', packaging: 'tar.gz', version: '$BUILD_NUMBER']]]
   }
*/
}

