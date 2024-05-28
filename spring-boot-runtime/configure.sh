#!/bin/bash
set -e

if ! which java > /dev/null; then
    echo Please install Java.
    exit 1
fi

if ! which git > /dev/null; then
    echo Please install git.
    exit 1
fi

export SPRING_BOOT_UPSTREAM=https://github.com/spring-projects/spring-boot
export GRADLE_USER_HOME=/tmp/gradle

release="$1"

if [[ ! $# -eq 1 ]]; then
    echo Usage: spring-boot.install '<version>'
    exit 1
fi

tempfile=$(mktemp -d)
trap 'rm -rf "$tempfile"' EXIT

cd ${tempfile}
git clone -b ${release} ${SPRING_BOOT_UPSTREAM} > /dev/null 2>&1 || echo Unable to pull Spring Boot version ${release}

cd spring-boot

cat >> build.gradle <<EOF
    task cacheToMavenLocal(type: Copy) {
        from new File(gradle.gradleUserHomeDir, 'caches/modules-2/files-2.1')
        into repositories.mavenLocal().url
        eachFile {
          List<String> parts = it.path.split('/')
          it.path =  parts[0].replace('.','/') + '/' + parts[1] + '/' + parts[2] + '/' + parts[4]
        }
        includeEmptyDirs false
      }
EOF

echo Building Spring Boot version ${release}
./gradlew -Dorg.gradle.daemon=false publishToMavenLocal cacheToMavenLocal
