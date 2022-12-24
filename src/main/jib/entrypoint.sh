#!/bin/sh

echo "The application starting..."
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "org.romancha.autofon.MainKt"  "$@"
