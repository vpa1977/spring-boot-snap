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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

public class App {

    private static Log LOG = LogFactory.getLog(App.class);
    private static final String CONTENT_SNAPS = "content-snaps";

    record ContentSnap(String name,
            String version,
            String summary,
            String description,
            String upstream) {

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            }
            if (other instanceof ContentSnap) {
                var otherSnap = (ContentSnap) other;
                if (name.equals(otherSnap.name)) {
                    return true;
                }
            }
            return false;
        }

    }

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
                var data = (Map<String, String>) snaps.get(name);

                snapList.add(
                        new ContentSnap(data.get("name"),
                        data.get("version"),
                        data.get("summary"),
                        data.get("description"),
                        data.get("upstream")
                        ));
            }
        }
        return snapList;
    }

    private static void writeContentSnap(ContentSnap snap) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: content-snaps <manifest-file>");
            System.exit(0);
        }
        // read manifest
        Set<ContentSnap> snaps = loadSnaps(args[0]);
        // for each entry
        for (ContentSnap snap  : snaps) {
            writeContentSnap(snap);
        }
        //   create content/<content-snap-name>

    }
}
