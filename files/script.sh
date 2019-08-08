function kubeswitch {
echo "kubectl switch from $1 to $2"
    echo "$2 install"
    kubectl apply -f $2/deployment.yml --namespace=apavarnitsyn
    kubectl apply -f $2/service.yml --namespace=apavarnitsyn
    echo "sleep"
    sleep 30
    TEST_CURL=$(curl -IL tomcat-$2-svc.apavarnitsyn.svc.k8s.playpit.by:8080/hello/)
    echo "$TEST_CURL"
    if [ $(echo "$TEST_CURL" | grep -c 'HTTP/1.1 200') -gt 0 ]

        
        then
            echo "heath-page checked"
            kubectl apply -f $2/ingress.yml --namespace=apavarnitsyn
            echo "Everything is OK. Clean up"
            kubectl delete -f $1/deployment.yml --namespace=apavarnitsyn
            kubectl delete -f $1/service.yml  --namespace=apavarnitsyn
            echo "Tomcat $2 installed succesfully"    
        else
            echo "ALARM! Traceback!"
            kubectl delete -f $2/deployment.yml --namespace=apavarnitsyn
            kubectl delete -f $2/service.yml --namespace=apavarnitsyn
    fi


}


# main
ls -ll
sudo apk add curl
TEST=$(kubectl get pods) 
if [ $(echo "$TEST" | grep -c 'tomcat-blue') -lt 1 ]
	then 
    kubeswitch green blue 
else
	kubeswitch blue green
fi 
