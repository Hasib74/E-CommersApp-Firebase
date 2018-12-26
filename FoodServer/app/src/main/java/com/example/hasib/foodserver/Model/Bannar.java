package com.example.hasib.foodserver.Model;

/**
 * Created by HASIB on 6/24/2018.
 */

public class Bannar {
    private String id;
    private  String name;
    private String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bannar(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public Bannar() {
    }

}
