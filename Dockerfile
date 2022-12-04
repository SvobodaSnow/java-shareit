FROM amazoncorretto:11-alpine-jdk
COPY server/target/*.jar server.jar
ENTRYPOINT ["java","-jar","/server.jar"]
