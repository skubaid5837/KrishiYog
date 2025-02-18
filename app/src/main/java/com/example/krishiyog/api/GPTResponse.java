package com.example.krishiyog.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GPTResponse {
        @SerializedName("choices")
        private List<Choice> choices;

        public String getContent() {
            if (choices != null && !choices.isEmpty() &&
                    choices.get(0).message != null &&
                    choices.get(0).message.content != null) {
                return choices.get(0).message.content;
            }
            return null;
        }

        static class Choice {
            @SerializedName("message")
            Message message;
        }

        static class Message {
            @SerializedName("content")
            String content;
        }
}
