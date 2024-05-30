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
import java.nio.file.Paths;

/**
 * @TODO i18n
 * @TODO handle maven user home
 * @TODO handle gradle user home
 */
public class App {
    public static void main(String[] args) throws Throwable {
        System.out.println(
                "This program will update ~/.m2/settings.xml to include a Spring Boot snap maven repositories.");
        File m2settings = new File(
                String.valueOf(Paths.get(System.getProperty("user.home"), ".m2")));
        Settings settings = new Settings(m2settings);
        if (!settings.addSpringBootProfile())
            System.out.println("Spring boot profile 'spring-boot-snap' is already present in maven user settings file");
        else
            System.out.println("Spring boot profile 'spring-boot-snap' was added to maven user settings file");

        System.out.println(
            "This program will add 'springbootsnap.gradle' to ~/.gradle/init.d to configure Spring Boot maven repository.");
        File gradleInitDir = new File(
            String.valueOf(Paths.get(System.getProperty("user.home"), ".gradle", "init.d")));
        GradleInit gradleInit = new GradleInit(gradleInitDir);
        if (!gradleInit.addSpringBootInitFile())
            System.out.println("Spring boot init file 'springbootsnap.gradle' was already added to "+ gradleInitDir.toString());
        else
            System.out.println("Spring boot init file 'springbootsnap.gradle' was added to "+ gradleInitDir.toString());
    }
}
