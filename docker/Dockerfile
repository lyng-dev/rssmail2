FROM maven:3.8.4-openjdk-17-slim

ARG finalName

WORKDIR /app

COPY . /app

RUN mvn package -Dproject.build.finalName=${finalName}

ENTRYPOINT [ "java", "-jar", "target/${finalName}.jar" ]