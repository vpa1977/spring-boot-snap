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


import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ManifestTest extends Manifest {

    @Test
    public void testLoadManifest() throws Exception {
        String manifest = """
                content-snaps:
                  spring-boot-33:
                    channel: latest/edge
                    mount: /foo
                  spring-framework-61:
                    channel: latest/edge
                    mount: /foo
                                """;

        Set<Snap> snaps = super.load(manifest);
        HashSet<Snap> expected = new HashSet<Snap>();
        expected.add(new Snap("spring-boot-33", "latest/edge", "/foo", false));
        expected.add(new Snap("spring-framework-61", "latest/edge", "/foo", false));
        MatcherAssert.assertThat(snaps, is(expected));
        Snap snap = snaps.iterator().next();
        assertEquals("latest/edge", snap.channel);
        assertEquals("/foo", snap.mount);
    }
}
