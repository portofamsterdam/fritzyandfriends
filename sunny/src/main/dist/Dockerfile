FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./sunny-app.jar /opt/sunny/sunny-app.jar

CMD java -jar /opt/sunny/sunny-app.jar server /opt/config/sunny/sunny.json

EXPOSE 8080 
EXPOSE 8081