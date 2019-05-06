FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./netty-app.jar /opt/netty/batty-app.jar
ADD ./netty.json /opt/config/netty.json

CMD java -jar /opt/netty/netty-app.jar server /opt/config/netty.json

EXPOSE 8080 
EXPOSE 8081