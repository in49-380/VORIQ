FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /src

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17 AS app-build
ARG RELEASE=17
ENV RELEASE=${RELEASE}
WORKDIR /opt/build

COPY --from=builder /src/target/*.jar ./application.jar
RUN java -Djarmode=layertools -jar application.jar extract

RUN set -eux; \
  BASE="$($JAVA_HOME/bin/jdeps \
      --ignore-missing-deps -q -recursive --multi-release ${RELEASE} \
      --print-module-deps \
      -cp 'dependencies/*:snapshot-dependencies/*' application.jar)"; \
  EXTRA="java.desktop,java.sql,java.instrument,java.transaction.xa,java.naming,java.security.sasl,java.security.jgss,java.management,jdk.crypto.ec,jdk.localedata,jdk.unsupported,jdk.security.auth"; \
  MODULES="$(printf '%s,%s' "$BASE" "$EXTRA" | tr -d '[:space:]')"; \
  echo "Using modules: $MODULES"; \
  $JAVA_HOME/bin/jlink --add-modules "$MODULES" \
      --strip-debug --no-man-pages --no-header-files --compress=2 \
      --output /opt/build/jdk

FROM debian:bookworm-slim

ARG BUILD_PATH=/opt/build
ENV JAVA_HOME=/opt/jdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED \
               --add-opens java.base/jdk.internal.misc=ALL-UNNAMED \
               --add-exports java.base/jdk.internal.misc=ALL-UNNAMED"

RUN apt-get update && apt-get install -y --no-install-recommends \
      ca-certificates tzdata wget \
  && rm -rf /var/lib/apt/lists/*

RUN groupadd --gid 1000 spring-app \
 && useradd  --uid 1000 --gid spring-app --shell /usr/sbin/nologin --create-home spring-app

WORKDIR /opt/app

COPY --from=app-build --chown=spring-app:spring-app $BUILD_PATH/jdk             $JAVA_HOME
COPY --from=app-build --chown=spring-app:spring-app $BUILD_PATH/application.jar ./application.jar

USER spring-app:spring-app

EXPOSE 8081
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar application.jar"]
