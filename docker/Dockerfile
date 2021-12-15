FROM maven:3.8.4-openjdk-17-slim

WORKDIR /app

COPY . /app

RUN ls -a 
RUN mvn package

ENTRYPOINT [ "java", "-jar", "target/rssmail-0.0.1-SNAPSHOT.jar" ]