# https://hub.docker.com/_/maven/tags?page=1&name=openjdk-17-
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /home/app

# 캐싱
COPY ./pom.xml /home/app/pom.xml
COPY ./src/main/java/com/example/orderservice/OrderServiceApplication.java /home/app/src/main/java/com/example/orderservice/OrderServiceApplication.java
RUN mvn -f /home/app/pom.xml clean compile package -DskipTests=true

# 캐싱
COPY . /home/app
RUN mvn -f /home/app/pom.xml clean compile package -DskipTests=true

FROM openjdk:17-ea-17-jdk-slim
COPY --from=build /home/app/target/*.jar OrderService.jar
COPY --from=build /home/app /home/app
ENTRYPOINT [ "java", "-jar", "OrderService.jar"]