package  _	
	class Hbledai{
	def steps
	def namesecret = ''
	def url_docker_repo = ''
	def username = ''
	def password = ''
	Hbledai(steps) {this.steps = steps}
	
	def createSecret (){
		steps.sh "kubectl create secret docker-registry ${name} --docker-server=${url} --docker-username=${username} --docker-password=${password}"
	}
}

return this