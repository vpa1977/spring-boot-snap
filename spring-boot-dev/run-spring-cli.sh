#!/bin/sh

if ! which java > /dev/null; then
    echo Please install Java.
    exit 1
fi

java -jar $SNAP/spring-cli/spring-cli-*.jar $*
