package  _	

	def steps
	def namesecret = ''
	def url_docker_repo = ''
	def username = ''
	def password = ''
	K8s(steps) {this.steps = steps}
	
	def createSecret (){
		sh "kubectl create secret docker-registry ${name} --docker-server=${url} --docker-username=${username} --docker-password=${password}"
	}


return this