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
package com.canonical.springboot.configure;

import lombok.NonNull;

public class Snap {

    @NonNull
    public String name;
    @NonNull
    public String channel;
    @NonNull
    public String mount;

    public boolean installed;

    public Snap(@NonNull String name, @NonNull String channel, @NonNull String mount,
            boolean installed) {
        this.name = name;
        this.installed = installed;
        this.channel = channel;
        this.mount = mount;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other instanceof Snap) {
            var otherSnap = (Snap) other;
            if (name.equals(otherSnap.name)) {
                return true;
            }
        }
        return false;
    }
}
