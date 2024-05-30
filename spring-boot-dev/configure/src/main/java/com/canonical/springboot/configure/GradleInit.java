/*
 * This file is part of Spring Boot snap.
 *
 * Copyright 2024 Canonical Ltd.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 3, as published by the
 * Free Software Foundation.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 3, as published by the
 * Free Software Foundation.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.canonical.springboot.configure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * This class adds spring-boot-snap.gradle
 * init script to $GRADLE_USER_HOME/init.d/ directory
 */
public class GradleInit {

    private final static String GRADLE_INIT_STRING = """

    apply plugin: SpringBootSnapRepositoryPlugin

    class SpringBootSnapRepositoryPlugin implements Plugin<Gradle> {

        void apply(Gradle gradle) {
            gradle.allprojects { project ->
                project.repositories {
                    // add the enterprise repository
                    maven {
                        name "Spring_Boot_Snap_Repository"
                        url "file:///snap/spring-boot/current/maven-repo/"
                    }
                }
            }
        }

    }
            """;

    private File m_gradleInitDir;

    public GradleInit(File gradleInitDir) throws IOException {
        if (gradleInitDir.exists())
            return;
        gradleInitDir.mkdirs();
        m_gradleInitDir = gradleInitDir;
    }

    public boolean addSpringBootInitFile() throws IOException {
        File settings = new File(m_gradleInitDir, "springbootsnap.gradle");
        if (settings.exists())
            return false;
        Files.writeString(settings.toPath(), GRADLE_INIT_STRING, StandardOpenOption.CREATE_NEW);
        return true;
    }


}
