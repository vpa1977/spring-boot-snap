#!/bin/sh

command=$1
if [ -z $command ]; then
    cat $SNAP/info.txt
    exit 0
fi
shift

case ${command} in
    available)
        devpack-for-spring.available $*
    ;;
    install)
        devpack-for-spring.install $*
    ;;
    list)
        devpack-for-spring.list $*
    ;;
    spring-cli)
        devpack-for-spring.spring-cli $*
    ;;
    spring)
        devpack-for-spring.spring $*
    ;;
    local-maven)
        devpack-for-spring.local-maven $*
    ;;
    setup-maven)
        devpack-for-spring.setup-maven
    ;;
    setup-gradle)
        devpack-for-spring.setup-gradle
    ;;
    *)
        cat $SNAP/info.txt
    ;;
esac
