#!/bin/sh
set -eu

DEFAULT_JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS="${JAVA_OPTS:-$DEFAULT_JAVA_OPTS}"

echo "Starting user-service with JVM options: $JAVA_OPTS"

# Intentional word splitting so each JVM flag is passed as a separate argument.
exec java $JAVA_OPTS -jar /app/app.jar
