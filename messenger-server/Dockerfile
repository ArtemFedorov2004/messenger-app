FROM openjdk:21

ARG JAR_FILE=target/messenger-server-*-exec.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

CMD ["java", "-jar","app.jar"]