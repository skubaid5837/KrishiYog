package com.example.krishiyog.models;

import java.util.List;

public class CartModel {
    String imageUrls;
    String productName;
    String productPrice;
    String productId;
    int quantity;
    String unit;
    String size;

    public CartModel(String imageUrls, String productName, String productPrice, String productId, int quantity, String unit, String size) {
        this.imageUrls = imageUrls;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productId = productId;
        this.quantity = quantity;
        this.unit = unit;
        this.size = size;
    }

    public CartModel() {
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
