package com.example.krishiyog.models;

public class ChatBotModel {
    private String message;
    private boolean isUser;

    public ChatBotModel(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public ChatBotModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}
