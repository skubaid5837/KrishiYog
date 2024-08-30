package com.example.krishiyog.models;

public class CategoriesModel {
    String category;
    int image;

    public CategoriesModel(String category, int image) {
        this.category = category;
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
