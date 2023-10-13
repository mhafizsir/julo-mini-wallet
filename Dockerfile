FROM maven:3.9.4-eclipse-temurin-17 as maven
MAINTAINER mhafizsir@gmail.com
COPY pom.xml /
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn clean package -DskipTests && cp target/*.jar *.jar

FROM eclipse-temurin:17-jre

WORKDIR /application
COPY --from=maven /*.jar ./*.jar
CMD echo "The application is starting..." && java -jar *.jar ${APP_CONFIG_ARGS}