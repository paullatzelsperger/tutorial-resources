#
#  Copyright (c) 2024 SAP SE
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       SAP SE - initial API and implementation
#
#

FROM eclipse-temurin:22.0.1_8-jre-alpine
ARG JAR
ARG ADDITIONAL_FILES

ARG APP_USER=docker
ARG APP_UID=10100

RUN addgroup --system "$APP_USER"

RUN adduser \
     --shell /sbin/nologin \
     --disabled-password \
     --gecos "" \
     --ingroup "$APP_USER" \
     --no-create-home \
     --uid "$APP_UID" \
     "$APP_USER"

USER "$APP_USER"
WORKDIR /app

COPY ${JAR} backend-service.jar

ENV WEB_HTTP_PORT="8080"
ENV WEB_HTTP_PATH="/api"

# health check is handled by K8S
HEALTHCHECK NONE

CMD ["java", \
     "-Dedc.fs.config=/app/configuration.properties", \
     "-Djava.util.logging.config.file=/app/logging.properties", \
     "-Djava.security.egd=file:/dev/urandom", \
     "-jar", \
     "backend-service.jar"]
