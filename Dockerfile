FROM openjdk:21-jdk-slim as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ./target/*.jar attachment-service.jar
RUN java -Djarmode=layertools -jar attachment-service.jar extract

FROM openjdk:21-jdk-slim
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8083
