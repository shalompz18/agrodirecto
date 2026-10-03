# ==========================================
# Etapa 1: Compilación y empaquetado (Build)
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copiar el descriptor del proyecto y descargar dependencias en caché
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar el empaquetado JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Etapa 2: Imagen de ejecución ligera (Runtime)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario y grupo sin privilegios por seguridad
RUN addgroup -S agrogroup && adduser -S agrouser -G agrogroup

# Crear el directorio de logs configurado en application.properties y asignar permisos
RUN mkdir -p /app/logs && chown -R agrouser:agrogroup /app

# Cambiar al usuario sin privilegios
USER agrouser

# Copiar el JAR generado desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto por defecto de la aplicación
EXPOSE 8088

# Comando de inicio del microservicio
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]