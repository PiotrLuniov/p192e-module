def call(String repo, String studentName){
	if (repo == 'MNT-pipeline-training'){
		nexusPublisher nexusInstanceId: 'nexus', 
			nexusRepositoryId: 'MNT-pipeline-training', 
			packages: [[$class: 'MavenPackage', 
				mavenAssetList: [[classifier: '', extension: '', filePath: "pipeline-${studentName}-\${BUILD_NUMBER}.tar.gz"]], 
				mavenCoordinate: [artifactId: "${studentName}", groupId: 'pipeline', packaging: '.tar.gz', version: '${BUILD_NUMBER}']]
		]
	}
	if(repo == 'docker'){
		withDockerRegistry(url: 'https://registry-ci.playpit.by') {
			sh "docker build -t registry-ci.playpit.by/helloworld-${studentName}:${BUILD_NUMBER} -f config/Dockerfile ."
			sh "docker push registry-ci.playpit.by/helloworld-${studentName}:${BUILD_NUMBER}"
		}
	}
}