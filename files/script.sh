function kubeswitch {
echo "kubectl switch from $1 to $2"
    echo "$2 install"
    sed -i "s/VERSION/$3/g" $2/deployment.yml
    kubectl apply -f $2/deployment.yml --namespace=apavarnitsyn
    kubectl apply -f $2/service.yml --namespace=apavarnitsyn
    echo "sleep"
    sleep 30
    echo "Running tests"
    COUNT=0
    TEST_KUBE=$(kubectl get pods --namespace=apavarnitsyn)
    if [ $(echo "$TEST_KUBE" | grep "tomcat-$2" | grep -c "Running") -gt 0 ]
    	then
    		echo "Container is running"
    		COUNT=$(( $COUNT+1 ))
    	else
    		echo "ALARM! Container is crashed"
    fi
    TEST_CURL=$(curl -IL tomcat-$2-svc.apavarnitsyn.svc.cluster.local:8080/)
    if [ $(echo "$TEST_CURL" | grep -c 'HTTP/1.1 200') -gt 0 ]
    	then
    		echo "Tomcat is running"
    		COUNT=$(( $COUNT+1 ))
    	else
    		echo "ALARM! Tomcat is crashed"
   	fi
    TEST_HELLO=$(curl -IL tomcat-$2-svc.apavarnitsyn.svc.cluster.local:8080/hello/)
    if [ $(echo "$TEST_HELLO" | grep -c 'HTTP/1.1 200') -gt 0 ]
    	then
    		echo "helloworld is running"
    		COUNT=$(( $COUNT+1 ))
    	else
    		echo "ALARM! Container is crashed"
   	fi
    TEST_TEST=$(curl tomcat-$2-svc.apavarnitsyn.svc.cluster.local:8080/hello/test.html)
    if [ $(echo "$TEST_TEST" | grep -c "$3") -gt 0 ]
    	then
    		echo "Page is ready"
    		COUNT=$(( $COUNT+1 ))
    	else
    		echo "ALARM! Health page is outdated"
    fi
    if [ $COUNT -eq 4 ]   
        then
            echo "Switch ingress"
            kubectl apply -f $2/ingress.yml --namespace=apavarnitsyn
            echo "Clean up"
            kubectl delete -f $1/deployment.yml --namespace=apavarnitsyn
            kubectl delete -f $1/service.yml  --namespace=apavarnitsyn
            echo "Tomcat $2 installed succesfully"    
        else
            echo "ALARM! Traceback!"
            kubectl delete -f $2/deployment.yml --namespace=apavarnitsyn
            kubectl delete -f $2/service.yml --namespace=apavarnitsyn
    fi
}


#main
TEST=$(kubectl get pods --namespace=apavarnitsyn) 
if [ $(echo "$TEST" | grep -c 'tomcat-blue') -lt 1 ]
	then 
    kubeswitch green blue $1
else
	kubeswitch blue green $1
fi 
