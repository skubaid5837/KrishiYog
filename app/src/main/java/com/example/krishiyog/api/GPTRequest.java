package com.example.krishiyog.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GPTRequest {
    @SerializedName("model")
    private String model;

    @SerializedName("messages")
    private List<Message> messages;

    @SerializedName("max_tokens")
    private int maxTokens;

    public GPTRequest(String base64Image) {
        this.model = "gpt-4o";
        this.maxTokens = 500;
        this.messages = new ArrayList<>();

        Message message = new Message();
        message.role = "user";
        message.content = new ArrayList<>();

        Content textContent = new Content();
        textContent.type = "text";
        textContent.text = "Analyze this image and tell me if this plant has any disease. If yes, what is the disease name and what are the precautions to be taken? Give the response in JSON format with disease and precautions as keys.";

        Content imageContent = new Content();
        imageContent.type = "image_url";
        imageContent.imageUrl = new ImageUrl();
        imageContent.imageUrl.url = "data:image/jpeg;base64," + base64Image;

        message.content.add(textContent);
        message.content.add(imageContent);

        this.messages.add(message);
    }

    static class Message {
        @SerializedName("role")
        String role;

        @SerializedName("content")
        List<Content> content;
    }

    static class Content {
        @SerializedName("type")
        String type;

        @SerializedName("text")
        String text;

        @SerializedName("image_url")
        ImageUrl imageUrl;
    }

    static class ImageUrl {
        @SerializedName("url")
        String url;
    }
}
