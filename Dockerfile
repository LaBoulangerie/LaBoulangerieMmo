FROM gradle:7.4.2-alpine AS builder
COPY src /usr/app/src
COPY gradle /usr/app/gradle
COPY settings.gradle.kts /usr/app
COPY gradlew /usr/app
COPY build.gradle.kts /usr/app

WORKDIR /usr/app/
RUN chmod +x ./gradlew
RUN ./gradlew assemble --stacktrace
RUN rm build/libs/LaBoulangerieMmo-*dev*.jar
RUN mv build/libs/LaBoulangerieMmo-*.jar build/libs/LaBoulangerieMmo.jar

FROM openjdk:17-alpine
WORKDIR /usr/app/
RUN echo "eula=true" > eula.txt
RUN wget -O paper.jar https://papermc.io/api/v2/projects/paper/versions/1.18.2/builds/379/downloads/paper-1.18.2-379.jar
COPY --from=builder /usr/app/build/libs/LaBoulangerieMmo.jar /usr/app/plugins/LaBoulangerieMmo.jar
EXPOSE 25565
ENTRYPOINT ["java", "-Xmx2048M", "-XX:+UseG1GC", "-XX:+DisableExplicitGC", "-jar", "paper.jar"]