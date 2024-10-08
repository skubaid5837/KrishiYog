package com.example.krishiyog.models;

public class ProductModel {
    int image;
    String productName;
    String productPrice;
    String productRating;

    public ProductModel(int image, String productName, String productPrice, String productRating) {
        this.image = image;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productRating = productRating;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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

    public String getProductRating() {
        return productRating;
    }

    public void setProductRating(String productRating) {
        this.productRating = productRating;
    }
}
