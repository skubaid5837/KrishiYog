package com.example.krishiyog.models;

import java.util.List;

public class PostModel {
    private String profileImage;
    private String username;
    private String postId;
    private String userId;
    private String location;
    private String date;
    private String postDescription;
    private String postTitle;
    List<String> images;
    private String likeCount;
    private boolean status;

    public PostModel() {
    }

    public PostModel(String profileImage, String username, String postId, String userId, String location, String date, String postDescription, String postTitle, List<String> images, String likeCount, boolean status) {
        this.profileImage = profileImage;
        this.username = username;
        this.postId = postId;
        this.userId = userId;
        this.location = location;
        this.date = date;
        this.postDescription = postDescription;
        this.postTitle = postTitle;
        this.images = images;
        this.likeCount = likeCount;
        this.status = status;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}



