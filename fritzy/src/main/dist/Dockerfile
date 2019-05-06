FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./librxtxSerial.so /usr/lib/librxtxSerial.so
ADD ./librxtxParallel.so /usr/lib/librxtxParallel.so

ADD ./fritzy-app.jar /opt/fritzy/fritzy-app.jar
ADD ./fritzy.json /opt/config/fritzy.json

CMD java -jar /opt/fritzy/fritzy-app.jar server /opt/config/fritzy.json

EXPOSE 8080 
EXPOSE 8081


