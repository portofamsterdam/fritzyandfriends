FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./build/sunny-app.jar /opt/batty/sunny-app.jar
ADD ./build/sunny.json /opt/config/sunny.json

CMD java -jar /opt/batty/sunny-app.jar server /opt/config/sunny.json

EXPOSE 8080 
EXPOSE 8081


