def call(String namesecret = 'regcred', String url = 'nexus-ci.playpit.by:6566', String username = 'admin', String password = 'admin123' ) {
		sh "kubectl create secret docker-registry ${name} --docker-server=${url} --docker-username=${username} --docker-password=${password}"

}
