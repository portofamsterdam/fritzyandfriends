FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./exxy-app.jar /opt/exxy/exxy-app.jar

CMD java -jar /opt/exxy/exxy-app.jar server /opt/config/exxy/exxy.json

EXPOSE 8080 
EXPOSE 8081