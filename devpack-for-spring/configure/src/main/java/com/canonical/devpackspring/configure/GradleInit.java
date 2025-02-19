/*
 * This file is part of Devpack for Spring® snap.
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

package com.canonical.devpackspring.configure;

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
            gradle.beforeSettings { settings ->
                settings.pluginManagement.repositories { handler ->
                    var repo = maven {
                            name "plugin-%s"
                            url "file:///snap/%s/current/maven-repo/"
                        }
                    handler.remove(repo)
                    handler.addFirst(repo)
                }
            }
            gradle.allprojects { project ->
                project.repositories {
                    maven {
                        name "%s"
                        url "file:///snap/%s/current/maven-repo/"
                    }
                }
            }
        }

    }
            """;

    private final String GRADLE_CENTRAL_PLUGIN = """
    beforeSettings { settings ->
        settings.pluginManagement.repositories {
            gradlePluginPortal()
        }
    }
    """;

    private File m_gradleInitDir;

    public GradleInit(File gradleInitDir) throws IOException {
        gradleInitDir.mkdirs();
        m_gradleInitDir = gradleInitDir;
    }

    public boolean addGradletInitFile(Snap snap) throws IOException {
        File settings = new File(m_gradleInitDir, snap.name() + ".gradle");
        if (settings.exists())
            return false;
        String initString = String.format(GRADLE_INIT_STRING, snap.name(), snap.name());
        Files.writeString(settings.toPath(), initString, StandardOpenOption.CREATE_NEW);

        File commonSettings = new File(m_gradleInitDir, "gradlePluginPortal.gradle");
        if (commonSettings.exists())
            return true;
        Files.writeString(commonSettings.toPath(), GRADLE_CENTRAL_PLUGIN, StandardOpenOption.CREATE_NEW);
        return true;
    }


}
