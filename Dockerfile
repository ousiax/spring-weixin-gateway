FROM eclipse-temurin:17 as builder
WORKDIR /build
COPY target/*.jar artifact.jar
RUN java -Djarmode=layertools -jar artifact.jar extract

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ ./

CMD ["java", "org.springframework.boot.loader.JarLauncher"]
