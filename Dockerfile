FROM tomcat:8.0
COPY helloworld-ws/target/helloworld-ws.war /usr/local/tomcat/webapps/
EXPOSE 8080
ENTRYPOINT ["/usr/local/tomcat/bin/catalina.sh","run"]
