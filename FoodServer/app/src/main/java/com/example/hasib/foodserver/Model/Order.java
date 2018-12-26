package com.example.hasib.foodserver.Model;

/**
 * Created by HASIB on 12/2/2017.
 */

public class Order {

    private String ProductId;
    private String ProductName;
    private String ProductPrice;
    private String Quantity;
    private String Discount;

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public Order(String productId, String productName, String productPrice, String quantity, String discount) {

        ProductId = productId;
        ProductName = productName;
        ProductPrice = productPrice;
        Quantity = quantity;
        Discount = discount;
    }

    public Order() {

    }



}
