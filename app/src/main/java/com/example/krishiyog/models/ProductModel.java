package com.example.krishiyog.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class ProductModel {
    List<String> imageUrls;
    String productName;
    String productPrice;
    int productRating;
    String productDescription;
    String productCategory;
    String productSize;
    String productUnit;
    String productId;
    String sellerId;

    public ProductModel(List<String> imageUrls, String productName, String productPrice, int productRating, String productDescription, String productCategory, String productSize, String productUnit, String productId, String sellerId) {
        this.imageUrls = imageUrls;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productRating = productRating;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productSize = productSize;
        this.productUnit = productUnit;
        this.productId = productId;
        this.sellerId = sellerId;
    }

    public ProductModel() {
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
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

    public int getProductRating() {
        return productRating;
    }

    public void setProductRating(int productRating) {
        this.productRating = productRating;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
