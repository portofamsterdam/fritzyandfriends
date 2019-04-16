FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./build/librxtxSerial.so /usr/lib/librxtxSerial.so
ADD ./build/librxtxParallel.so /usr/lib/librxtxParallel.so

ADD ./build/fritzy-app.jar /opt/appgw/fritzy-app.jar
ADD ./build/fritzy.json /opt/config/fritzy.json

CMD java -jar /opt/appgw/fritzy-app.jar server /opt/config/fritzy.json

EXPOSE 8080 
EXPOSE 8081


