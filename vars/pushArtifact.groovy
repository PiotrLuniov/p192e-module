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
		withDockerRegistry(url: 'http://nexus-ci.playpit.by:6566') {
			sh "docker build -t nexus-ci.playpit.by:6566/helloworld-${studentName}:${BUILD_NUMBER} -f config/Dockerfile ."
			sh "docker push nexus-ci.playpit.by:6566/helloworld-${studentName}:${BUILD_NUMBER}"
		}
	}
}