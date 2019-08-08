def call(def namesecret = 'regcred', def url = 'nexus-ci.playpit.by:6566', def username = 'admin', def password = 'admin123' ) {
		sh "kubectl create secret docker-registry ${namesecret} --docker-server=${url} --docker-username=${username} --docker-password=${password}"

}
