FROM maven:3.8.4-openjdk-17-slim

WORKDIR /app

COPY . /app

dockRUN mvn package

ENTRYPOINT [ "java", "-jar", "target/rssmail.jar" ]