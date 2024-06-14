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

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;

public class SettingsTest {
    @Test
    public void testCreateSettings() throws Exception {
        File f = File.createTempFile("prefix", "suffix");
        f.delete();
        File settingsFile = new File(f, "settings.xml");

        Settings settings = new Settings(f);
        Assert.assertEquals(
                """
                        <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                        <settings xmlns="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
                            <profiles/>
                        </settings>
                            """,
                settings.toXml());
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(settingsFile))) {
            wr.write(settings.toXml());
        }

        Settings other = new Settings(f);
        Assert.assertEquals(settings.toXml(), other.toXml());
        f.delete();
    }

    @Test
    public void testAddMavenProfile() throws Exception {
        Snap snap = new Snap("foo", "latest/edge", "/foo", false);
        File f = File.createTempFile("prefix", "suffix");
        f.delete();
        File settingsFile = new File(f, "settings.xml");
        Settings settings = new Settings(f);
        Assert.assertTrue(settings.addMavenProfile(snap));
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(settingsFile))) {
            wr.write(settings.toXml());
        }
        Settings other = new Settings(f);
        Assert.assertFalse(other.addMavenProfile(snap));
        f.delete();
    }
}
