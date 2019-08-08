node {
      def STUDENT = 'ymlechka'

    stage ('Preparation'){
        checkout scm
          //([$class: 'GitSCM', branches: [[name: "*/$STUDENT"]], \
        //userRemoteConfigs: [[url: ' https://github.com/MNT-Lab/p192e-module']]])
    }
    stage ('Building code'){
        withMaven(jdk: 'JDK9', maven: 'Maven 3.6.1'){
            sh 'mvn clean package -f helloworld-ws/pom.xml'
    }
    }


}
