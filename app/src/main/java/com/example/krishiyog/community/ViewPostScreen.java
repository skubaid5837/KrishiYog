package com.example.krishiyog.community;

import static com.example.krishiyog.chatBot.ChatBotScreen.OPENAI_API_URL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
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
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.krishiyog.R;
import com.example.krishiyog.adapters.CommentAdapter;
import com.example.krishiyog.chatBot.ChatBotScreen;
import com.example.krishiyog.databinding.ActivityViewPostScreenBinding;
import com.example.krishiyog.models.CommentModel;
import com.example.krishiyog.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPostScreen extends AppCompatActivity {

    ActivityViewPostScreenBinding binding;
    private String postId;
    FirebaseFirestore db;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    List<CommentModel> commentModelList;
    CommentAdapter commentAdapter;
    boolean isHindi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityViewPostScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        String description = binding.postDescription.getText().toString();

        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.sendComment.setOnClickListener(view -> {
            String comment = binding.commentInput.getText().toString().trim();
            if (!comment.isEmpty()) {
                sendCommentToDatabase(postId, comment); // Your function to save comment
                binding.commentInput.setText(""); // Clear input
                binding.commentSection.setVisibility(View.GONE);
            }
        });

        binding.translate.setOnClickListener(view -> {
            if (isHindi){
                binding.translate.setTextColor(getResources().getColor(R.color.black));
                isHindi = false;
                binding.postDescription.setText(description);
            }else {
                binding.translate.setTextColor(getResources().getColor(R.color.secondaryBlack));
                isHindi = true;
                translateTextToHindi(description);
            }
        });

        //bind data
        getPostData();

        //Recycler View
        setRecyclerView();
    }

    private void translateTextToHindi(String text) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("temperature", 0.7);
            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a professional translator. Translate the given text into Hindi.");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", text);

            messages.put(systemMessage);
            messages.put(userMessage);
            jsonBody.put("messages", messages);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    ChatBotScreen.OPENAI_API_URL,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray choices = response.getJSONArray("choices");
                                String translatedText = choices.getJSONObject(0).getJSONObject("message").getString("content");

                                // Update UI with translated text
                                binding.postDescription.setText(translatedText);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ViewPostScreen.this, "Translation error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ViewPostScreen.this, "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + ChatBotScreen.OPENAI_API_KEY);
                    return headers;
                }
            };

            // Add request to Volley queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendCommentToDatabase(String postId, String comment) {
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("name");
                    // Generate unique ID
                    String commentId = db.collection("posts").document().collection("comments").document().getId();

                    CommentModel commentModel = new CommentModel(commentId, userId, userName, "Mumbai", comment, String.valueOf(new Date()),"2","1");

                    db.collection("posts").document(postId)
                            .collection("comments").document(commentId)
                            .set(commentModel)
                            .addOnSuccessListener(documentReference -> {
                                // ✅ Update UI Immediately After Comment is Added
                                commentModelList.add(0, commentModel); // Add new comment at the top
                                commentAdapter.notifyItemInserted(0); // Notify adapter of the new comment
                                binding.commentRv.smoothScrollToPosition(0); // Scroll to top

                                // Clear input field and hide keyboard
                                binding.commentInput.setText("");
                                binding.commentSection.setVisibility(View.GONE);
                                hideKeyboard();
                            });

                });
    }

    // Hide Keyboard Function
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.commentInput.getWindowToken(), 0);
        }
    }

    private void setRecyclerView() {
        binding.commentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.commentRv.hasFixedSize();
        commentModelList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentModelList);
        binding.commentRv.setAdapter(commentAdapter);

        fetchComment();
    }

    private void fetchComment() {
        db.collection("posts")
                .document(postId)
                .collection("comments")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(!documentSnapshot.isEmpty()){
                        commentModelList.clear();
                        for (DocumentSnapshot snapshot : documentSnapshot){
                            CommentModel commentModel = snapshot.toObject(CommentModel.class);
                            if(commentModel != null){
                                commentModelList.add(commentModel);
                            }
                        }
                        commentAdapter.updateData(commentModelList);
                    }
                });
    }

    private void getPostData() {
        db.collection("posts")
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        PostModel postModel = documentSnapshot.toObject(PostModel.class);
                        if (postModel != null) {
                            bindPostData(postModel);
                        }
                    }
                });
    }

    private void bindPostData(PostModel postModel) {
        binding.username.setText(postModel.getUsername());
        binding.likeCount.setText(postModel.getLikeCount());
        binding.postDescription.setText(postModel.getPostDescription());
//        Glide.with(this)
//                .load(postModel.getProfileImage())
//                .into(binding.profileImage);
        Picasso.get().load(postModel.getProfileImage()).into(binding.profileImage);
        binding.date.setText(postModel.getLocation()+" "+postModel.getDate());

        for(int i=0 ; i<postModel.getImages().size() ; i++){
            slideModels.add(new SlideModel(postModel.getImages().get(i), ScaleTypes.FIT));
        }
        binding.postImages.setImageList(slideModels);
        binding.postImages.setSlideAnimation(AnimationTypes.DEPTH_SLIDE);

    }

}