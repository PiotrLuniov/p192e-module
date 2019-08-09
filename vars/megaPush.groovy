def call(String artifactType, String studentName){
	if (artifactType == "maven"){
		nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'MNT-pipeline-training', \
			packages: [[$class: 'MavenPackage', \
				mavenAssetList: [[classifier: '', extension: '', \
					filePath: "pipeline-${studentName}-\${BUILD_NUMBER}.tar.gz"]], \
				mavenCoordinate: [artifactId: "${studentName}", groupId: 'pipeline', \
					packaging: 'tar.gz', version: '${BUILD_NUMBER}'] \
			]]
	}
	if (artifactType == "docker"){
		withDockerRegistry(credentialsId: 'nexus', toolName: 'dockerTool', \
					url: 'http://nexus-ci.playpit.by:6566') {
		sh "docker build -t nexus-ci.playpit.by:6566/helloworld-${studentName}:${BUILD_NUMBER} -f Dockerfile ."
		sh "docker push nexus-ci.playpit.by:6566/helloworld-${studentName}:${BUILD_NUMBER}"
		}
	}
}
