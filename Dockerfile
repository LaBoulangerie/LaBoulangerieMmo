FROM maven:3.6-adoptopenjdk-8 AS builder
MAINTAINER TheHunter365
COPY src /usr/app/src
COPY pom.xml /usr/app
RUN mvn -f /usr/app/pom.xml clean package

FROM openjdk:8-jre-alpine
MAINTAINER TheHunter365
WORKDIR /usr/app/
RUN echo "eula=true" > eula.txt
RUN wget -O paper.jar https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/645/downloads/paper-1.16.5-645.jar
COPY --from=builder /usr/app/target/LaBoulangerieMmo.jar /usr/app/plugins/LaBoulangerieMmo.jar
EXPOSE 25565
ENTRYPOINT ["java", "-Xmx2048M", "-XX:+UseG1GC", "-XX:+DisableExplicitGC", "-jar", "paper.jar"]