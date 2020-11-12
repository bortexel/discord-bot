FROM java:8

ARG BOT_TOKEN

ADD target/BortexelBot-1.0.jar .
CMD java -jar BortexelBot-1.0.jar
