FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./netty-app.jar /opt/netty/natty-app.jar
ADD ./netty.json /opt/config/netty.json

CMD java -jar /opt/netty/netty-app.jar server /opt/config/netty.json

EXPOSE 8080 
EXPOSE 8081