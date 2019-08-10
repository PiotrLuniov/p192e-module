
def url = 'nexus-ci.playpit.by:6566'
def deployFile ( String container_name, 
				String creds = 'dockerrepo', 
				String file_name = 'deploy_tomcat.yml', 
				String app_name = 'helloworld-ws', 
				String container_port = '8080'

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
        image: registry-ci.playpit.by/${container_name}
        ports:
        - containerPort: ${container_port}
        #readinessProbe:
         # exec:
          #  command:
           # - grep
            #- '-q'
            #- 'helloworld-ws' 
            #- /usr/local/tomcat/webapps/helloworld-ws/healthz.html
          #httpGet:
           # path: /${app_name}
            #port: ${container_port}
          #initialDelaySeconds: 3
          #periodSeconds: 3
        livenessProbe:
          httpGet:
            path: /${app_name}
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

def serviceFile(def file_name = 'service_tomcat.yaml', def name_service = 'tomcat-svc',  def port = '8080', def targetPort = '8080'){
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
def ingressFile ( def file_name = 'ingress_tomcat.yaml', def ingress_name = 'tomcat-ingress', 
	def name_service = 'tomcat-svc', def port = '8080' ) {
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
def kubectl_apply (def file, def namespace = 'hbledai'){
sh "kubectl apply -n ${namespace} -f ${file}"
}
return this 
