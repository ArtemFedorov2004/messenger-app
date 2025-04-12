FROM openjdk:21

ARG JAR_FILE=target/messenger-server-*-exec.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENV MONGO_DB_USERNAME=${MONGO_DB_USERNAME}
ENV MONGO_DB_PASSWORD=${MONGO_DB_PASSWORD}
ENV MONGO_DB_HOST=${MONGO_DB_HOST}

CMD ["java", "-jar","-Dspring.data.mongodb.username=${MONGO_DB_USERNAME}","-Dspring.data.mongodb.password=${MONGO_DB_PASSWORD}","-Dspring.data.mongodb.host=${MONGO_DB_HOST}","app.jar"]