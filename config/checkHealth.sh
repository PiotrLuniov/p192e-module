#!/bin/sh

oldVersion=$(curl -s localhost:8080/helloworld-ws/test.html|head -n1|cut -d' ' -f2);
if [[ ${oldVersion} != "_buildNumber_" ]]
then
	exit 1
fi
exit 0