FROM openjdk:11-slim

ENV SBT_VERSION 1.8.0

RUN export DEBIAN_FRONTEND="noninteractive" \
    && apt-get update \
    && apt-get --quiet --yes dist-upgrade \
    && apt-get install --yes curl \
    && curl -sSLo sbt-$SBT_VERSION.deb https://repo.scala-sbt.org/scalasbt/debian/sbt-$SBT_VERSION.deb \
    && apt-get install --yes ./sbt-$SBT_VERSION.deb \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && rm sbt-$SBT_VERSION.deb

WORKDIR /app

COPY ./http/target/scala-2.13/http-assembly-0.1.0-SNAPSHOT.jar /app/app.jar
COPY dockerfiles/http-server/entrypoint.sh /entrypoint.sh

EXPOSE 8000
ENTRYPOINT ["/entrypoint.sh"]