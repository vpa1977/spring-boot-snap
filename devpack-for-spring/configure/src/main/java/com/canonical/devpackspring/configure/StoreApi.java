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

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class StoreApi {
    private static final String ROOT_URL = "http://api.snapcraft.io/v2/snaps/info/%s";
    public static final String UNKNOWN = "Unknown";

    public static SnapDescription querySnap(String name, String channel) {
        try {
            try (Client client = ClientBuilder.newClient()) {
                WebTarget main = client.target(String.format(ROOT_URL, name));
                var builder = main.request(MediaType.APPLICATION_JSON);
                builder.header("Snap-Device-Series", "16");
                builder.header("Snap-Device-Store", "ubuntu");
                var response = builder.get(JsonObject.class);
                String summary = ((JsonObject) response.get("snap")).getString("summary");
                var channelMap = ((JsonArray) response.get("channel-map"));
                String version = UNKNOWN;
                for (var entry : channelMap) {
                    JsonObject obj = (JsonObject) entry.asJsonObject().get("channel");
                    if (channel.equals(obj.getString("track") + "/" + obj.getString("risk"))) {
                        version = entry.asJsonObject().getString("version");
                        break;
                    }
                }
                return new SnapDescription(summary, version);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return new SnapDescription(null, UNKNOWN);
    }
}
