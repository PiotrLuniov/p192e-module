def url = 'nexus-ci.playpit.by:6566'
def deployFile ( def container_name, 
				def creds = 'dockerrepo', 
				def file_name = 'deploy_tomcat.yaml', 
				def app_name = 'helloworld-ws', 
				def container_port = '8080'){
	
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
        - containerPort: 8080
        readinessProbe:
          httpGet:
            path: /${app_name}
            port: ${container_name}
          initialDelaySeconds: 5
          periodSeconds: 5
          successThreshold: 1
      imagePullSecrets:
      - name: ${creds}

	"""
	return ${file_name}
}
def serviceFile(def file_name = 'service_tomcat.yaml',def name_service = 'tomcat-svc',  def port = '8080', def targetPort = '8080'){
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
return ${file_name}
}
def ingressFile ( def file_name = 'ingress_tomcat.yaml', def ingress_name = 'tomcat-ingress', def name_service = 'tomcat-svc', def port = '8080' ) {
	sh """
cat << EOF > ${file_name}
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ${ingress_name}
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /

spec:
  rules:
  - host: hbledai.k8s.playpit.by
    http:
      paths:
      - path: /
        backend:
          serviceName: ${name_service}
          servicePort: ${port}
EOF
	"""
	return ${file_name}
}
def kubectl_apply (def file, def namespace = 'hbledai'){
sh "kubectl apply -n ${namespace} ${file}"
}
return this 