FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./apxprices-app.jar /opt/apxprices/apxprices-app.jar
ADD ./apxprices.json /opt/config/apxprices.json

CMD java -jar /opt/apxprices/apxprices-app.jar server /opt/config/apxprices.json

EXPOSE 8080 
EXPOSE 8081


