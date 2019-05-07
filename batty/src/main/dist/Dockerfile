FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./batty-app.jar /opt/batty/batty-app.jar
ADD ./truststore.jks /opt/batty/truststore.jks
ADD ./batty.json /opt/config/batty.json

CMD java -jar /opt/batty/batty-app.jar server /opt/config/batty.json

EXPOSE 8080 
EXPOSE 8081


