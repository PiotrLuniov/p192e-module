node {
      def STUDENT = 'ymlechka'

    stage ('Preparation'){
        checkout([$class: 'GitSCM', branches: [[name: "*/$STUDENT"]], \
        userRemoteConfigs: [[url: ' https://github.com/MNT-Lab/p192e-module']]])
    }





}
