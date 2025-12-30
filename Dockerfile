# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

# Project sources
COPY src ./src

# External libs
COPY lib ./lib

COPY config.json* ./

RUN mkdir -p classes \
    && find src -name "*.java" > sources.txt \
    && javac -encoding UTF-8 -cp "lib/*" -d classes @sources.txt

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# App classes + libs
COPY --from=build /build/classes ./classes
COPY --from=build /build/lib ./lib
COPY --from=build /build/config.json* ./

RUN mkdir -p /app/logs

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "classes:lib/*", "ru.yandex.practicum.api.ServerHMAC"]
CMD ["./config.json"]