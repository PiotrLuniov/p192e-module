def createSecret (def namesecret = 'regcred', def url = 'nexus-ci.playpit.by:6566', def username = 'admin', def password = 'admin123'){
		sh '''
		if [ ! $(kubectl get ${namesecret} -n hbledai | grep -q ${namesecret} && echo $?) ]
    then
		 "kubectl create secret docker-registry ${namesecret} --docker-server=${url} --docker-username=${username} --docker-password=${password}"
	fi
	'''
	}

return this 