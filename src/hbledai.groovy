	package _
	def namesecret = ''
	def url_docker_repo = ''
	def username = ''
	def password = ''

	def create_secret (){
		sh "kubectl create secret docker-registry ${name} --docker-server=${url} --docker-username=${username} --docker-password=${password}"
	}

return this