FROM amazoncorretto:11-alpine-jdk
COPY gateway/target/*.jar geteway.jar
COPY server/target/*.jar server.jar
ENTRYPOINT ["java","-jar","/geteway.jar"]
ENTRYPOINT ["java","-jar","/server.jar"]
