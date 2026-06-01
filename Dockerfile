FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_DATASOURCE_URL="jdbc:mysql://clima_db:3306/clima_app?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
ENV SPRING_DATASOURCE_USERNAME="root"
ENV SPRING_DATASOURCE_PASSWORD="parde4"
ENV WEATHER_API_KEY="aaa1b2d610dbc18cbacdf4c32273c324"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
