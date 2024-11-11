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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import org.yaml.snakeyaml.Yaml;

public class Manifest {

    public static final String SNAP = "/snap/";
    private static final String CONTENT_SNAPS = "content-snaps";

    private boolean isInstalled(String name) {
        return new File(SNAP + name + "/current/").exists();
    }

    protected Set<Snap> load(String manifest) throws IOException {
        HashSet<Snap> snapList = new HashSet<Snap>();
        Yaml yaml = new Yaml();
        try (InputStream is = new ByteArrayInputStream(manifest.getBytes())) {
            Map<String, Object> raw = yaml.load(is);
            @SuppressWarnings("unchecked")
            Map<String, Object> snaps = (Map<String, Object>) raw.get(CONTENT_SNAPS);
            if (snaps == null) {
                throw new IOException("Manifest misses 'content-snaps' tag");
            }
            for (var name : snaps.keySet()) {
                @SuppressWarnings("unchecked")
                var data = (Map<String, String>) snaps.get(name);

                snapList.add(
                        new Snap(name, data.get("channel"), data.get("mount"), isInstalled(name)));
            }
        }
        return snapList;
    }
}
