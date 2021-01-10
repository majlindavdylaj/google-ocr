package com.koddev.googleocr.model;

public class Bucket {

    private String name;
    private String lastImage;

    public Bucket(String name, String lastImage) {
        this.name = name;
        this.lastImage = lastImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastImage() {
        return lastImage;
    }

    public void setLastImage(String lastImage) {
        this.lastImage = lastImage;
    }
}