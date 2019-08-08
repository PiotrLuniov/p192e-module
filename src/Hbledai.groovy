	/*class K8s{
	def steps
	def namesecret = ''
	def url_docker_repo = ''
	def username = ''
	def password = ''
	K8s(steps) {this.steps = steps}
	
	def createSecret (){
		steps.sh "kubectl create secret docker-registry ${name} --docker-server=${url} --docker-username=${username} --docker-password=${password}"
	}
}

return this*/
package _
class Utilities implements Serializable {
  def steps
  Utilities(steps) {this.steps = steps}
  def mvn(args) {
    steps.sh "${steps.tool 'Maven'}/bin/mvn -o ${args}"
  }
}