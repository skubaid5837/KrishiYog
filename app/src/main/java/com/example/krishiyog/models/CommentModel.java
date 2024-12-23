package com.example.krishiyog.models;

import java.util.Date;

public class CommentModel {
    private String commentId;
    private String userId;
    private String username;
    private String location;
    private String commentText;
    private String timestamp;
    private String likes;
    private String dislikes;

    public CommentModel() {
    }

    public CommentModel(String commentId, String userId, String username, String location, String commentText, String timestamp, String likes, String dislikes) {
        this.commentId = commentId;
        this.userId = userId;
        this.username = username;
        this.location = location;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }
}
