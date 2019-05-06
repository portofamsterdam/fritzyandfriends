FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./sunny-app.jar /opt/batty/sunny-app.jar
ADD ./sunny.json /opt/config/sunny.json

CMD java -jar /opt/batty/sunny-app.jar server /opt/config/sunny.json

EXPOSE 8080 
EXPOSE 8081


