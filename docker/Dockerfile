FROM openjdk:17-alpine

WORKDIR /app

COPY target/rssmail*.jar rssmail.jar

ENTRYPOINT [ "java", "-jar", "rssmail.jar" ]