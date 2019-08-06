node('Host-Node'){
	def studentName = 'adalimayeu'
	stage('Preparation (Checking out)'){
		echo "Preparation (Checking out)"

		git branch: "${studentName}", url: 'https://github.com/MNT-Lab/p192e-module.git'
	}
	stage('Building code'){
		echo "Building code"
	}
	stage('Sonar scan'){
		echo "Sonar scan"
	}
	stage('Testing'){
		echo "Testing"
	}
	stage('Triggering job and fetching artefact after finishing'){
		echo "Triggering job and fetching artefact after finishing"
	}
	stage('Packaging and Publishing results'){
		echo "Packaging and Publishing results"
	}
	stage('Asking for manual approval'){
		echo "Asking for manual approval"
	}
	stage('Deployment'){
		echo "Deployment"
	}

}