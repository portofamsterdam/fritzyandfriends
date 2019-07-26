FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./netty-app.jar /opt/netty/netty-app.jar

CMD java -jar /opt/netty/netty-app.jar server /opt/config/netty/netty.json

EXPOSE 8080 
EXPOSE 8081