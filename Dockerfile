FROM openjdk:11.0-jre

WORKDIR /home/container
ADD target/BortexelBot-1.0.jar .
CMD java -jar BortexelBot-1.0.jar
