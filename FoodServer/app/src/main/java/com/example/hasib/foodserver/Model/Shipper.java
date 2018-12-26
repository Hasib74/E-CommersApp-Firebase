package com.example.hasib.foodserver.Model;

/**
 * Created by HASIB on 6/28/2018.
 */

public class Shipper {
    public Shipper(String name, String number, String password) {
        this.name = name;
        this.number = number;
        this.password = password;
    }

    public Shipper() {
    }

    private String name,number,password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
