package com.example.krishiyog.chatBot;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
    private boolean isKeyboardVisible = false;

    private static final String OPENAI_API_KEY = "";
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

        // Setup keyboard visibility listener
        setupKeyboardVisibilityListener();

        // Setup message input handling
        setupMessageInput();

        chatBotModelArrayList = new ArrayList<>();
        chatBotAdapter = new ChatBotAdapter(chatBotModelArrayList);

        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(chatBotAdapter);

        // Move click listener setup to separate method
        setupSendButton();

    }

    private void setupSendButton() {
        binding.sendBtn.setOnClickListener(view -> handleSendMessage());
    }

    private void setupMessageInput() {
        binding.message.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {

                handleSendMessage();
                return true;
            }
            return false;
        });
    }

    private void handleSendMessage() {
        String userMessage = binding.message.getText().toString().trim();

        if (TextUtils.isEmpty(userMessage)) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message to RecyclerView
        addMessageToChat(userMessage, true);
        binding.message.setText("");

        // Hide keyboard after sending
        hideKeyboard();

        // Reset translations with animation
        binding.bottomLayout.animate()
                .translationY(0)
                .setDuration(200)
                .start();
        binding.chatRecyclerView.animate()
                .translationY(0)
                .setDuration(200)
                .start();

        // Fetch chatbot response
        fetchChatBotResponse(userMessage);
    }

    private void setupKeyboardVisibilityListener() {
        // Store original position
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                binding.getRoot().getWindowVisibleDisplayFrame(r);
                int screenHeight = binding.getRoot().getRootView().getHeight();
                int keyboardHeight = screenHeight - r.bottom;

                // If keyboard height is more than 15% of screen height,
                // consider keyboard as visible
                boolean isKeyboardNowVisible = keyboardHeight > screenHeight * 0.15;

                if (isKeyboardNowVisible != isKeyboardVisible) {
                    isKeyboardVisible = isKeyboardNowVisible;

                    if (isKeyboardVisible) {
                        // Keyboard is being shown
                        binding.bottomLayout.setTranslationY(-keyboardHeight);
                        binding.chatRecyclerView.setTranslationY(-keyboardHeight);
                        scrollToBottom();
                    } else {
                        // Keyboard is being hidden
                        binding.bottomLayout.animate()
                                .translationY(0)
                                .setDuration(200)
                                .start();
                        binding.chatRecyclerView.animate()
                                .translationY(0)
                                .setDuration(200)
                                .start();
                    }
                }
            }
        });

        // Handle focus changes
        binding.message.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Reset translation when focus is lost
                binding.bottomLayout.animate()
                        .translationY(0)
                        .setDuration(200)
                        .start();
                binding.chatRecyclerView.animate()
                        .translationY(0)
                        .setDuration(200)
                        .start();
            }
        });

        // Handle touch events
        binding.message.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!isKeyboardVisible) {
                    // Show keyboard if it's not visible
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.message, InputMethodManager.SHOW_IMPLICIT);
                }
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardVisible) {
            // If keyboard is visible, hide it first
            hideKeyboard();
            binding.bottomLayout.animate()
                    .translationY(0)
                    .setDuration(200)
                    .start();
            binding.chatRecyclerView.animate()
                    .translationY(0)
                    .setDuration(200)
                    .start();
        } else {
            super.onBackPressed();
        }
    }

    private void scrollToBottom() {
        if (chatBotModelArrayList.size() > 0) {
            binding.chatRecyclerView.smoothScrollToPosition(chatBotModelArrayList.size() - 1);
        }
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
                            // Toast.makeText(ChatBotScreen.this, botReply, Toast.LENGTH_SHORT).show();
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.message.getWindowToken(), 0);
        }
    }

}