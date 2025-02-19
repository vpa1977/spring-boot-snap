/*
 * This file is part of Devpack for Spring® snap.
 *
 * Copyright 2025 Canonical Ltd.
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
package com.canonical.devpackspring.content;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.StringSubstitutor;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class App {

    private static final String CONTENT_SNAPS = "content-snaps";
    private static Log LOG = LogFactory.getLog(App.class);

    private static Set<ContentSnap> loadSnaps(String path) throws IOException {
        Yaml yaml = new Yaml();
        HashSet<ContentSnap> snapList = new HashSet<ContentSnap>();
        byte[] manifest = Files.readAllBytes(Path.of(path));
        try (InputStream is = new ByteArrayInputStream(manifest)) {
            Map<String, Object> raw = yaml.load(is);
            @SuppressWarnings("unchecked")
            Map<String, Object> snaps = (Map<String, Object>) raw.get(CONTENT_SNAPS);
            if (snaps == null) {
                throw new IOException("Manifest does not contain 'content-snaps' tag");
            }
            for (var name : snaps.keySet()) {
                @SuppressWarnings("unchecked")
                var data = (Map<String, Object>) snaps.get(name);
                if (data.get("tool") instanceof Boolean b && b) {
                    LOG.info("Skipping " + data.get("name"));
                    continue;
                }
                var snap = (Map<String,String>)snaps.get(name);
                snapList.add(
                        new ContentSnap(snap.get("name"),
                                snap.get("version"),
                                snap.get("summary"),
                                snap.get("description"),
                                snap.get("upstream"),
                                snap.get("license"),
                                snap.getOrDefault("build-jdk", "openjdk-17-jdk-headless"),
                                snap.getOrDefault("extra-command", "")
                        ));
            }
        }
        return snapList;
    }

    private static void writeContentSnap(ContentSnap snap, Path destination) throws IOException {
        LOG.info("Writing content snap " + snap.name + " version " + snap.version);
        String contents = readResource("snapcraft.yaml.template");
        String gradleInit = readResource("init.gradle");
        StringSubstitutor replacer = new StringSubstitutor(snap.getReplacements());
        String snapcraftYaml = replacer.replace(contents);
        File snapcraftDir = new File(destination.toFile(), snap.name);
        if (!snapcraftDir.exists() && !snapcraftDir.mkdirs())
            throw new IOException("Unable to create " + snapcraftDir.getAbsolutePath());
        File snapDir = new File(snapcraftDir, "snap");
        if (!snapDir.exists() && !snapDir.mkdirs())
            throw new IOException("Unable to create " + snapcraftDir.getAbsolutePath());

        Files.writeString(new File(snapDir, "snapcraft.yaml").toPath(), snapcraftYaml);
        Files.writeString(new File(snapcraftDir, "init.gradle").toPath(), gradleInit);
    }

    private static String readResource(String resource) throws IOException {
        StringBuilder contents = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(App.class.getResourceAsStream(resource)))) {
            String line = null;
            while ((line = r.readLine()) != null) {
                contents.append(line);
                contents.append("\n");
            }
        }
        return contents.toString();
    }

    public static void main(String[] args) throws IOException {

        Options options = new Options();
        Option installOption = Option.builder("m").longOpt("manifest").argName("manifest").hasArg()
                .required()
                .desc("content snap manifest").build();
        options.addOption(installOption);

        Option destination = Option.builder("d").longOpt("destination").argName("directory").hasArg()
                .required(false)
                .desc("Generate snaps in <destination> directory")
                .build();
        options.addOption(destination);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("m")) {
                String manifest = cmd.getOptionValue("m");
                Set<ContentSnap> snaps = loadSnaps(manifest);
                for (ContentSnap snap : snaps) {
                    writeContentSnap(snap, Path.of(cmd.getOptionValue("d", "content")));
                }
            } else {
                throw new ParseException("Missing -m, print help");
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("generate", options);
            System.exit(-1);
        }
    }

    record ContentSnap(String name,
                       String version,
                       String summary,
                       String description,
                       String upstream,
                       String license,
                       String build_jdk,
                       String extra_command) {


        public Map<String, String> getReplacements() {

            Map<String, String> map = new HashMap<String, String>();
            map.put("name", name);
            map.put("version", version);
            map.put("summary", summary);
            map.put("description", multiLineDescription(description));
            map.put("upstream", upstream);
            map.put("license", license);
            map.put("build-jdk", build_jdk);
            map.put("extra-command", extra_command);
            return map;
        }

        public String multiLineDescription(String str) {
            StringBuilder output = new StringBuilder();
            StringTokenizer tk = new StringTokenizer(str, "\n");
            while (tk.hasMoreTokens()) {
                output.append("  ")
                        .append(tk.nextToken());
            }
            return output.toString();
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other instanceof ContentSnap otherSnap) {
                return name.equals(otherSnap.name);
            }
            return false;
        }
    }
}
