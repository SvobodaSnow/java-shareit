FROM amazoncorretto:11-alpine-jdk
COPY geteway/target/*.jar geteway.jar
COPY server/target/*.jar server.jar
ENTRYPOINT ["java","-jar","/geteway.jar"]
ENTRYPOINT ["java","-jar","/server.jar"]
