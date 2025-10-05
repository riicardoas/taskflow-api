# Etapa 1: build
FROM public.ecr.aws/docker/library/maven:3-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar pom.xml e baixar dependências para cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código-fonte e compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM public.ecr.aws/amazoncorretto/amazoncorretto:21
WORKDIR /app

# Copiar o jar final da build
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
