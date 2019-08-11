
def url = 'nexus-ci.playpit.by:6566'
def deployFile ( String container_name, 
				String creds ,  
				String app_name , 
				String container_port ,
				String file_name = 'deploy_tomcat.yml'

){

sh """
cat << EOF > ${file_name}
apiVersion: extensions/v1beta1 
kind: Deployment
metadata:
  name: tomcat
  labels:
    app: tomcat
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 0
      maxSurge: 1
  replicas: 1
  selector:
    matchLabels:
      app: tomcat
  template:
    metadata:
      labels:
        app: tomcat
    spec:
      containers:
      - name: tomcat
        image: ${container_name}
        ports:
        - containerPort: ${container_port}
        livenessProbe:
        
          #exec:
            #command:
            #- grep
            #- helloworld-ws 
            #- /usr/local/tomcat/webapps/helloworld-ws/healthz.html
          httpGet:
            path: /${app_name}/healthz.html
            port: ${container_port}
          initialDelaySeconds: 3
          periodSeconds: 3
        readinessProbe:
          tcpSocket:
            port: ${container_port}
          initialDelaySeconds: 3
          periodSeconds: 3      

      imagePullSecrets:
      - name: ${creds}
EOF
"""
return file_name
}
def deployFileTemplate ( def container_name, 
				def creds = 'dockerrepo', 
				def file_name = 'deploy_tomcat.yml', 
				def app_name = 'helloworld-ws', 
				def container_port = '8080'){

/*def f = new File('deploy_tomcat.template')
def engine = new groovy.text.GStringTemplateEngine()
def temp = engine.createTemplate(f).make(binding)
return temp.toString()*/





Templates = template.Must(template.ParseFiles("deploy_tomcat.template")) 

}

def serviceFile( String name_service ,  String port , String targetPort , String file_name = 'service_tomcat.yaml'){
sh """
cat << EOF > ${file_name}
apiVersion: v1
kind: Service
metadata:
  name: ${name_service}
spec:
  type: LoadBalancer
  ports:
    - port: ${port}
      targetPort: ${targetPort}
  selector:
    app: tomcat
EOF
"""
return file_name
}
def ingressFile (  String ingress_name , 
	String name_service , def port = '8080', String file_name = 'ingress_tomcat.yaml' ) {
	sh """
cat << EOF > ${file_name}
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ${ingress_name}
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /helloworld-ws/\\\$1 

spec:
  rules:
  - host: hbledai.k8s.playpit.by
    http:
      paths:
      - path: /(.*)
        backend:
          serviceName: ${name_service}
          servicePort: ${port}
EOF
	"""
	return file_name
}
def kubectl_apply (String file, String namespace = 'hbledai'){
sh "kubectl apply -n ${namespace} -f ${file}"
}
return this 
