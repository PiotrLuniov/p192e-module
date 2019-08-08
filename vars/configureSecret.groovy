def call(String namesecret, String url , String username , String password  ) {
		namesecret = 'regcred'
		url = 'nexus-ci.playpit.by:6566'
		username = 'admin'
		password = 'admin123'
		sh "kubectl create secret docker-registry ${name} --docker-server=${url} --docker-username=${username} --docker-password=${password}"

}
