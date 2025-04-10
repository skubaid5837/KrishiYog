package com.example.krishiyog.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.krishiyog.api.ApiService;
import com.example.krishiyog.api.GPTRequest;
import com.example.krishiyog.api.GPTResponse;
import com.example.krishiyog.api.RetroFitClient;
import com.example.krishiyog.chatBot.ChatBotScreen;
import com.example.krishiyog.databinding.FragmentScanBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanFragment extends Fragment {
    FragmentScanBinding binding;
    private File imageFile;
    Uri imageUri2;
    private Uri currentImageUri;
    private ApiService apiService;
    private static final int REQUEST_CODE_IMAGE_PICKER = 100;

    public ScanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        apiService = RetroFitClient.getApiService();

        binding.btnGallery.setOnClickListener(view -> {
            pickImage(false);
        });

        binding.btnCamera.setOnClickListener(view -> {
            pickImage(true);
        });

        binding.btnDetect.setOnClickListener(view -> {
            uploadImage();
        });

        binding.chatBot.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), ChatBotScreen.class);
            startActivity(i);
        });

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void pickImage(boolean isCamera) {
        ImagePicker.Builder builder = ImagePicker.with(this)
                .crop()
                .compress(512)
                .maxResultSize(1080, 1080);

        // Apply camera or gallery mode
        if (isCamera) {
            builder.cameraOnly();
        } else {
            builder.galleryOnly();
        }

        builder.start(REQUEST_CODE_IMAGE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageUri2 = imageUri;
            if (imageUri != null && binding != null && binding.imageView != null) {
                try {
                    // Set the image directly from URI
                    binding.imageView.setImageURI(null); // Clear the previous image
                    binding.imageView.setImageURI(imageUri);

                    // Get the actual file from the URI using the app's cache directory
                    String fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
                    File cacheDir = requireContext().getCacheDir();
                    imageFile = new File(cacheDir, fileName);

                    // Copy the content from URI to our file
                    try {
                        copyUriToFile(imageUri, imageFile);
                        // If we reach here, everything was successful
                        binding.btnDetect.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Add this helper method to your class
    private void copyUriToFile(Uri uri, File destinationFile) throws IOException {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
             OutputStream os = new FileOutputStream(destinationFile)) {
            if (is == null) {
                throw new IOException("Failed to open input stream");
            }
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        }
    }


    private void uploadImage() {
        String apiKey = "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";
        String authorization = "Bearer " + apiKey;
        if (binding.imageView.getDrawable() == null) {
            Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnDetect.setEnabled(false);

        try {
            // Get bitmap from ImageView using different method
            Bitmap bitmap = null;
            try {
                // First try: Get from drawable
                if (binding.imageView.getDrawable() instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) binding.imageView.getDrawable()).getBitmap();
                    Log.d("ImageProcessing", "Got bitmap from BitmapDrawable");
                }
            } catch (Exception e) {
                Log.e("ImageProcessing", "Error getting bitmap from drawable: " + e.getMessage());
            }

            // If first method failed, try second method
            if (bitmap == null) {
                try {
                    // Second try: Create bitmap from ImageView
                    binding.imageView.buildDrawingCache();
                    bitmap = binding.imageView.getDrawingCache();
                    Log.d("ImageProcessing", "Got bitmap from drawing cache");
                } catch (Exception e) {
                    Log.e("ImageProcessing", "Error getting bitmap from drawing cache: " + e.getMessage());
                }
            }

            // If both methods failed, try third method with URI
            if (bitmap == null && currentImageUri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().getContentResolver(),
                            currentImageUri
                    );
                    Log.d("ImageProcessing", "Got bitmap from URI");
                } catch (Exception e) {
                    Log.e("ImageProcessing", "Error getting bitmap from URI: " + e.getMessage());
                }
            }

            // Check if we got a bitmap
            if (bitmap == null) {
                throw new Exception("Failed to get bitmap from image");
            }

            // Resize bitmap if too large
            int maxSize = 1024;
            if (bitmap.getWidth() > maxSize || bitmap.getHeight() > maxSize) {
                float ratio = Math.min(
                        (float) maxSize / bitmap.getWidth(),
                        (float) maxSize / bitmap.getHeight()
                );
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        Math.round(bitmap.getWidth() * ratio),
                        Math.round(bitmap.getHeight() * ratio),
                        true
                );
                Log.d("ImageProcessing", "Resized bitmap to: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            }

            // Convert bitmap to base64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Log.d("ImageProcessing", "Base64 string length: " + base64Image.length());

            // Create request
            GPTRequest request = new GPTRequest(base64Image);

            // Make API call
            Call<GPTResponse> call = apiService.detectDisease(authorization, request);
            call.enqueue(new Callback<GPTResponse>() {
                @Override
                public void onResponse(@NonNull Call<GPTResponse> call, @NonNull Response<GPTResponse> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnDetect.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        String jsonString = response.body().getContent();
                        Log.d("APIResponse", "Raw response: " + jsonString);

                        // Extract JSON part if necessary
                        jsonString = extractJson(jsonString);
                        try {
                            JSONObject json = new JSONObject(jsonString);
                            String disease = json.getString("disease");
                            String precautions = json.getString("precautions");
                            String result = "Disease: " + disease;
                            showResult(result, disease);
                        } catch (Exception e) {
                            Log.e("APIError", "Error parsing response: " + e.getMessage());
                            Toast.makeText(getContext(), "Error analyzing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Unknown error";
                        Log.e("APIError", "Error response: " + errorBody);
                        Toast.makeText(getContext(), "API Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GPTResponse> call, @NonNull Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnDetect.setEnabled(true);
                    Log.e("APIError", "Network error: " + t.getMessage());
                    Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnDetect.setEnabled(true);
            Log.e("ImageProcessing", "Error processing image: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showResult(String result, String disease) {
        // You can customize how to display the result
        // For now, using Toast for demonstration
        binding.diseaseName.setText(result);
        if (!disease.equalsIgnoreCase("Unknown")){
            binding.askBot.setVisibility(View.VISIBLE);
        }
        binding.askBot.setOnClickListener(view -> {
            redirectToBot(disease);
        });

    }

    private void redirectToBot(String disease) {
        Intent i = new Intent(getContext(), ChatBotScreen.class);
        i.putExtra("disease", disease);
        i.putExtra("isTrue", "1");
        startActivity(i);
    }

    private String extractJson(String response) {
        // Find first '{' and last '}'
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        return response; // Return as-is if no JSON found
    }

}