package com.example.krishiyog.chatBot;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.krishiyog.R;
import com.example.krishiyog.adapters.ChatBotAdapter;
import com.example.krishiyog.databinding.ActivityChatBotScreenBinding;
import com.example.krishiyog.models.ChatBotModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatBotScreen extends AppCompatActivity {

    ActivityChatBotScreenBinding binding;
    private ArrayList<ChatBotModel> chatBotModelArrayList;
    private ChatBotAdapter chatBotAdapter;

    private static final String OPENAI_API_KEY = "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";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBotScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatBotModelArrayList = new ArrayList<>();
        chatBotAdapter = new ChatBotAdapter(chatBotModelArrayList);

        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(chatBotAdapter);

        binding.sendBtn.setOnClickListener(view -> {
            String userMessage = binding.message.getText().toString().trim();

            if (TextUtils.isEmpty(userMessage)) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add user message to RecyclerView
            addMessageToChat(userMessage, true);
            binding.message.setText("");

            // Fetch chatbot response
            fetchChatBotResponse(userMessage);

        });

    }

    private void addMessageToChat(String userMessage, boolean b) {
        chatBotModelArrayList.add(new ChatBotModel(userMessage, b));
        chatBotAdapter.notifyItemInserted(chatBotModelArrayList.size() - 1);
        binding.chatRecyclerView.scrollToPosition(chatBotModelArrayList.size() - 1);
    }

    private void fetchChatBotResponse(String userMessage) {
        // Prepare JSON body for the OpenAI API request
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("temperature", 0.7);
            JSONArray messages = new JSONArray();
            JSONObject userMessageJson = new JSONObject();
            userMessageJson.put("role", "user");
            userMessageJson.put("content", userMessage);
            messages.put(userMessageJson);
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a JSON request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                OPENAI_API_URL,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray choices = response.getJSONArray("choices");
                            JSONObject choice = choices.getJSONObject(0);
                            String botReply = choice.getJSONObject("message").getString("content");
                            Toast.makeText(ChatBotScreen.this, botReply, Toast.LENGTH_SHORT).show();

                            // Add chatbot response to RecyclerView
                            addMessageToChat(botReply, false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChatBotScreen.this, "Failed to parse response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String errorMessage = new String(error.networkResponse.data);
                            Toast.makeText(ChatBotScreen.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatBotScreen.this, "Network error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + OPENAI_API_KEY);
                return headers;
            }
        };

        // Add request to Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}