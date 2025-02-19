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
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GradleInitTest {

    @TempDir
    private File outputDir;

    @Test
    public void testWriteGradleInit() throws IOException {
        var snap = new Snap("foo", "1.1", "edge", "/mnt", "foobar", false);
        var init = new GradleInit(outputDir);
        init.addGradletInitFile(snap);
        String result = Files.readString(Path.of(outputDir.getAbsolutePath(), "foo.gradle"));
        assertTrue(result.contains("url \"file:///snap/foo/current/maven-repo/\""));
        assertTrue(result.contains("name \"foo\""));
    }
}
