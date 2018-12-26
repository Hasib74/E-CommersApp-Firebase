package com.example.hasib.foodserver.Model;

/**
 * Created by HASIB on 11/30/2017.
 */

public class Catagory {
    private String Name;
    private String Image;

    public Catagory(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getName() {

        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Catagory() {

    }
}
