FROM tomcat:latest
COPY helloworld-project/helloworld-ws/target/helloworld-ws-$BUILD_NUMBER.war /usr/local/tomcat/webapps/
EXPOSE 8081
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh", "run"]

