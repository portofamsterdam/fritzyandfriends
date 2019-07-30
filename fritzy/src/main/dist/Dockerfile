FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./librxtxSerial.so /usr/lib/librxtxSerial.so

ADD ./fritzy-app.jar /opt/fritzy/fritzy-app.jar

CMD java -jar /opt/fritzy/fritzy-app.jar server /opt/config/fritzy/fritzy.json

EXPOSE 8080 
EXPOSE 8081