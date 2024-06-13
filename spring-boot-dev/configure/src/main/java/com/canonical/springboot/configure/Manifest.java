package com.canonical.springboot.configure;


import java.io.File;
import java.util.List;

public class Manifest {


    class Snap {}

    public Manifest() {
        // temporary implementation - read embedded manifest file
        File f = new File("manifest/manifest.yaml");
    }

    public List<Snap> getContentSnaps() {
        return null;
    }



}
