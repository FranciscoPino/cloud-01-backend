FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Maven deposita el JAR ejecutable en la carpeta 'target/'
COPY target/*.jar app.jar

# Crear un usuario sin privilegios por seguridad en producción
RUN useradd -m springuser && chown -R springuser /app
USER springuser

# Spring Boot escucha en el puerto 8080 dentro del contenedor
EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]
