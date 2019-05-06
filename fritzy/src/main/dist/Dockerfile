FROM docker-proxy.intranet.technolution.nl/java:openjdk-8-jdk

ADD ./build/fritzy/librxtxSerial.so /usr/lib/librxtxSerial.so
ADD ./build/fritzy/librxtxParallel.so /usr/lib/librxtxParallel.so

ADD ./build/fritzy/fritzy-app.jar /opt/fritzy/fritzy-app.jar
ADD ./build/fritzy/fritzy.json /opt/config/fritzy.json

CMD java -jar /opt/fritzy/fritzy-app.jar server /opt/config/fritzy.json

EXPOSE 8080 
EXPOSE 8081


