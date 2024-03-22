#!/bin/sh
set -ex

export SPRING_BOOT_UPSTREAM=https://github.com/spring-projects/spring-boot
export JAVA_HOME=$SNAP/usr/lib/jvm/java-17-openjdk-amd64/
export GRADLE_USER_HOME=/tmp/gradle

release="$1"

cd /tmp/spring-boot
git clone -b ${release} ${SPRING_BOOT_UPSTREAM}


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

./gradlew publishToMavenLocal cacheToMavenLocal
