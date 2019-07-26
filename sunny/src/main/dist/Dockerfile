FROM docker-proxy.intranet.technolution.nl/openjdk:8-jre-alpine3.9

ADD ./sunny-app.jar /opt/batty/sunny-app.jar

# NOTE: The jsse.enableSNIExtension=false is needed, see FRI-46.
CMD java -Djsse.enableSNIExtension=false -jar /opt/batty/sunny-app.jar server /opt/config/sunny/sunny.json

EXPOSE 8080 
EXPOSE 8081