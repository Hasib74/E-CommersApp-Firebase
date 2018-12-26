package com.example.dcl.foodshipperapplicaation.Model;

import java.util.List;

/**
 * Created by HASIB on 6/30/2018.
 */

public class Request {
    private  String phone;
    private String name;
    private String total;
    private  String address;
    private List<Order> foods;

    public Request() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatLng() {
        return LatLng;
    }

    public void setLatLng(String latLng) {
        LatLng = latLng;
    }

    private String status;
    private String LatLng;

    public Request(String phone, String name, String total, String address, List<Order> foods, String status, String latLng) {
        this.phone = phone;
        this.name = name;
        this.total = total;
        this.address = address;
        this.foods = foods;
        this.status = status;
        LatLng = latLng;
    }




}