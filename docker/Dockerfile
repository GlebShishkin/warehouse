FROM openjdk:17
ARG APP_JAR=*.jar
COPY ${APP_JAR} appl.jar
ENTRYPOINT ["java", "-jar", "appl.jar"]
EXPOSE 8080