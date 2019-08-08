node {
      def STUDENT = 'ymlechka'

    stage ('Preparation'){
        checkout scm
          //([$class: 'GitSCM', branches: [[name: "*/$STUDENT"]], \
        //userRemoteConfigs: [[url: ' https://github.com/MNT-Lab/p192e-module']]])
              }
      
     stage('Creation metadata page'){
            sh label: '', script: '''builddate=$(date)
            cat << EOF > helloworld-ws/src/main/webapp/metadata.html
            build: $BUILD_NUMBER <br>
            author: ymlechka <br>
            build_url: $BUILD_URL <br>
            buils_data: $builddate
            EOF'''
    }
                
      
    stage ('Building code'){
        withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
            sh 'mvn clean package -f helloworld-ws/pom.xml'
    }
    }

    stage('Sonar scan') {
            def scannerHome = tool 'SonarQubeScanner'
            withSonarQubeEnv('sonar-ci') {
                sh "${scannerHome}/bin/sonar-scanner " +
                        "-Dsonar.projectKey=helloworld-ws-$STUDENT "+
                        '-Dsonar.language=java '+
                        '-Dsonar.sources=helloworld-ws/src '+
                        '-Dsonar.java.binaries=helloworld-ws/target'
            }
        }
      
      
      
      
      
      
      
      
      
      
      
}
