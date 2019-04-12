FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./src/main/resource/librxtxSerial.so /usr/lib/librxtxSerial.so
ADD ./src/main/resource/librxtxParallel.so /usr/lib/librxtxParallel.so

ADD ./build/fritzy-app.jar /opt/appgw/fritzy-app.jar
ADD ./build/conf.json /opt/config/conf.json

CMD java -jar ~/ApplicationGateway.jar server /opt/config/conf.json

EXPOSE 8080 
EXPOSE 8081


