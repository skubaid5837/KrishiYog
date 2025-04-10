package com.example.krishiyog.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.krishiyog.R;
import com.example.krishiyog.chatBot.ChatBotScreen;
import com.example.krishiyog.community.ViewPostScreen;
import com.example.krishiyog.databinding.CommentCardviewBinding;
import com.example.krishiyog.models.CommentModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    List<CommentModel> commentModelList;

    public CommentAdapter(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CommentCardviewBinding binding = CommentCardviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        CommentModel commentModel = commentModelList.get(position);
        holder.bind(commentModel);
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public void updateData(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CommentCardviewBinding binding;
        boolean isHindi = false;

        public ViewHolder(CommentCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CommentModel commentModel){

            binding.username.setText(commentModel.getUsername());

            String rawTimestamp = commentModel.getTimestamp(); // Example: "Fri, 09 Feb 2024 10:15:30 GMT"

            try {
                // Define input format matching your timestamp
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // Ensure correct parsing

                // Parse the timestamp into a Date object
                Date date = inputFormat.parse(rawTimestamp);

                // Define output format (without GMT)
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
                outputFormat.setTimeZone(TimeZone.getDefault()); // Convert to device local timezone

                String formattedDate = outputFormat.format(date); // Format the date

                binding.timestamp.setText(formattedDate);
            } catch (Exception e) {
                e.printStackTrace();
                binding.timestamp.setText("Invalid Date");
            }

            binding.location.setText(commentModel.getLocation());
            binding.comment.setText(commentModel.getCommentText());
            ViewPostScreen viewPostScreen = new ViewPostScreen();

            binding.translate.setOnClickListener(view -> {
                if (isHindi){
                    binding.translate.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));
                    binding.comment.setText(commentModel.getCommentText());
                    isHindi = false;
                }else {
                    binding.translate.setTextColor(ContextCompat.getColor(view.getContext(), R.color.secondaryBlack));
                    translateTextToHindi(commentModel.getCommentText());
                    isHindi = true;
                }
            });

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                            .document(commentModel.getUserId())
                                    .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()){
                                                    String url = documentSnapshot.getString("profilePhotoUrl");
                                                    if (url == "" || url == null){
                                                        Picasso.get().load(com.denzcoskun.imageslider.R.drawable.default_placeholder).into(binding.profileImage);
//                                                        Glide.with(itemView.getContext())
//                                                                .load(com.denzcoskun.imageslider.R.drawable.default_placeholder)
//                                                                .into(binding.profileImage);
                                                    }else {
                                                        Picasso.get().load(url).into(binding.profileImage);
//                                                        Glide.with(itemView.getContext())
//                                                                .load(url)
//                                                                .into(binding.profileImage);
                                                    }
                                                }
                                            });
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
                                    binding.comment.setText(translatedText);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(itemView.getContext(), "Translation error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(itemView.getContext(), "API Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
                requestQueue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
