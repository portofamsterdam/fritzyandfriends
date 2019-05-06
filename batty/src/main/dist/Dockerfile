FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./batty-app.jar /opt/batty/batty-app.jar
ADD ./truststore.jks /opt/batty/truststore.jks

ADD ./build/batty.json /opt/config/batty.json

CMD java -jar /opt/batty/batty-app.jar server /opt/config/batty.json

EXPOSE 8080 
EXPOSE 8081


