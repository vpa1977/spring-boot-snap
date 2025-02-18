package com.canonical.devpackspring.content;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestApp {

    @TempDir
    private Path testDir;

    @Test
    public void testGenerateManifest() throws IOException {
        String manifest = """
content-snaps:
  content-for-spring-boot-34:
    upstream: https://github.com/spring-projects/spring-boot
    version: 3.4.2
    channel: latest/edge
    mount: /maven-repo
    oss-eol: 2025-12-31
    name: content-for-spring-boot-34
    summary:  Rebuild of Spring® Boot Framework sources v3.4.x
    description: |
      Rebuild of Spring® Boot Framework sources v3.4.x

      Spring is a trademark of Broadcom Inc. and/or its subsidiaries.
    license: Apache-2.0
    build-jdk: openjdk-17-jdk-headless
    lts: false
                """;
        Path testManifest = testDir.resolve("manifest.yaml");
        Files.writeString( testManifest, manifest);
        App.main(new String[] {"-m" , testManifest.toString(), "-d", testDir.toString()});

        Path contentSnapPath = testDir.resolve("content-for-spring-boot-34");
        Path gradleInitPath = contentSnapPath.resolve("init.gradle");
        Path snapcraftYaml = contentSnapPath.resolve("snap/snapcraft.yaml");

        assertTrue(gradleInitPath.toFile().exists());
        assertTrue(snapcraftYaml.toFile().exists());
    }

    @Test
    @Disabled
    void testBuildSnap() throws IOException, InterruptedException {
        Path contentSnapPath = testDir.resolve("content-for-spring-boot-34");
        testGenerateManifest();
        Process snapcraft = new ProcessBuilder("snapcraft")
                .directory(contentSnapPath.toFile())
                .inheritIO()
                .start();
        int ret = snapcraft.waitFor();
        assertEquals(0, ret);
        Path builtSnapPath = contentSnapPath.resolve("content-for-spring-boot-34_3.4.2_amd64.snap");
        assertTrue(builtSnapPath.toFile().exists());
    }
}
