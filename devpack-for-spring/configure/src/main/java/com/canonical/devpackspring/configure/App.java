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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

/**
 * @TODO i18n
 * @TODO handle maven user home
 * @TODO handle gradle user home
 * @TODO mount under SNAP_COMMON
 */
public class App {

    private static Log LOG = LogFactory.getLog(App.class);

    private static String loadManifest() throws IOException {
        StringBuffer ret = new StringBuffer();
        String where = System.getenv("SNAP");
        if (where == null)
            where = "";
        else
            where +="/";

        try (BufferedReader r = new BufferedReader(new FileReader(where + "manifest/manifest.yaml"))) {
            String line;
            while ((line = r.readLine()) != null) {
                ret.append(line);
                ret.append("\n");
            }
        }
        return ret.toString();
    }

    private static void list(final boolean installed) throws IOException {
        var manifest = new Manifest();
        var snaps = manifest.load(loadManifest());
        snaps = snaps.stream().filter(x -> installed == x.installed()).collect(Collectors.toSet());
        if (snaps.isEmpty()) {
            System.out.println("\t None");
            return;
        }
        for (var item : snaps) {
            SnapDescription desc = StoreApi.querySnap(item.name(), item.channel());
            System.out.println("\t- " + item.name() + " channel " + item.channel() + " version "+ desc.version());
            System.out.println("\t  " + desc.description());
        }
    }

    private static void list() throws IOException {
        System.out.println("Installed content snaps:");
        list(true);
        System.out.println("Available content snaps:");
        list(false);
    }

    private static void refresh() throws Throwable {
        var manifest = new Manifest();
        var snaps = manifest.load(loadManifest());
        for (var snap : snaps) {
            if (!snap.installed())
                continue;
            populateCache(snap);
        }
    }

    private static void install(final String snapName) throws Throwable {

        // read manifest
        // find snap in manifest
        var manifest = new Manifest();
        var snaps = manifest.load(loadManifest());
        snaps = snaps.stream().filter(x -> x.name().equals(snapName)).collect(Collectors.toSet());
        if (snaps.size() != 1) {
            LOG.error(snapName + " is not available");
            return;
        }

        Snap snap = snaps.iterator().next();
        if (snap.installed()) {
            LOG.error(snapName + " is already installed");
            return;
        }
        // install snap
        LOG.info("Running: snap install " + snapName + " --channel=" + snap.channel());
        ProcessBuilder pb =
                new ProcessBuilder("snap", "install", snapName, "--channel=" + snap.channel());
        pb.inheritIO();
        Process p = pb.start();
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            LOG.error("Failed to install snap " + snapName);
        }

        setupMaven(snap);
        setupGradle(snap);
    }

    private static void populateCache(Snap snap) throws IOException {
        var from = String.format("/snap/%s/current/maven-repo/", snap.name());
        var to = String.format("%s/.m2/repository", System.getProperty("user.home"));
        recursiveCopy(Path.of(from), Path.of(to));
    }

    private static void recursiveCopy(Path of, Path to) throws IOException {
        LOG.info("copy from "+ of + " to "+ to);
        for (var file : of.toFile().listFiles()) {
            var other = new File(to.toFile(), file.getName());
            if (file.isDirectory()) {
                other.mkdirs();
                recursiveCopy(file.toPath(), other.toPath());
            } else {
                Files.copy(file.toPath(), other.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static void setupGradle(Snap snap) throws IOException {
        LOG.info(
                "This program will add '"+ snap.name()+".gradle' to ~/.gradle/init.d to configure Spring Boot maven repository.");
        File gradleInitDir = new File(
                String.valueOf(Paths.get(System.getProperty("user.home"), ".gradle", "init.d")));
        GradleInit gradleInit = new GradleInit(gradleInitDir);
        if (!gradleInit.addGradletInitFile(snap))
            LOG.info("Spring boot init file '"+ snap.name()+".gradle' was already added to "
                    + gradleInitDir);
        else
            LOG.info("Spring boot init file '"+ snap.name()+".gradle' was added to "
                    + gradleInitDir);
    }

    private static void setupMaven(Snap snap) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
        File m2settings =
                new File(String.valueOf(Paths.get(System.getProperty("user.home"), ".m2")));
        Settings settings = new Settings(m2settings);
        if (!settings.addMavenProfile(snap))
            LOG.info(
                    "Spring boot profile '"+ snap.name()+"' is already present in maven user settings file");
        else {
            File settingsFile = new File(m2settings, "settings.xml");
            try (BufferedWriter wr = new BufferedWriter(new FileWriter(settingsFile))) {
                wr.write(settings.toXml());
            }
            LOG.info(
                    "Spring boot profile '"+ snap.name()+"' was added to maven user settings file");
        }
    }

    public static void main(String[] args) throws Throwable {

        Options options = new Options();
        options.addOption("l", "list", false, "list installed content snaps");

        Option installOption = Option.builder("i").longOpt("install").argName("snap").hasArg()
                .desc("install a content snap").build();
        options.addOption(installOption);

        Option refreshOption = Option.builder("r").longOpt("refresh").desc("populate local Maven repository").build();
        options.addOption(refreshOption);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("l")) {
                list();
            } else if (cmd.hasOption("i")) {
                String snap = cmd.getOptionValue("i");
                install(snap);
            } else if (cmd.hasOption("r")) {
                refresh();
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("install", options);
            }
        } catch (ParseException e) {
            System.out.println("usage: install <snap-name>");
            System.exit(-1);
        }
    }
}
