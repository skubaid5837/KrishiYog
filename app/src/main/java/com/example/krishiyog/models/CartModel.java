package com.example.krishiyog.models;

import java.util.List;

public class CartModel {
    String imageUrls;
    String productName;
    String productPrice;
    String productId;
    int quantity;

    public CartModel(String imageUrls, String productName, String productPrice, String productId, int quantity) {
        this.imageUrls = imageUrls;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartModel() {
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
