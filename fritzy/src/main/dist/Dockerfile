FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./librxtxSerial.so /usr/lib/jvm/java-1.8-openjdk/jre/lib/amd64/librxtxSerial.so
ADD ./RXTXcomm.jar /usr/lib/jvm/java-1.8-openjdk/jre/lib/ext/RXTXcomm.jar

ADD ./fritzy-app.jar /opt/fritzy/fritzy-app.jar

CMD java -jar /opt/fritzy/fritzy-app.jar server /opt/config/fritzy/fritzy.json

EXPOSE 8080 
