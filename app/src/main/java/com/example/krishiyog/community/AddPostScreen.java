package com.example.krishiyog.community;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.krishiyog.models.PostModel;
import com.google.android.material.snackbar.Snackbar;
import com.example.krishiyog.databinding.ActivityAddPostScreenBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AddPostScreen extends AppCompatActivity {
    private ActivityAddPostScreenBinding binding;
    private final ArrayList<Uri> selectedFiles = new ArrayList<>();
    private final List<String> uploadedImageUrls = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private StorageReference storageReference;
    private static final int MAX_FILES = 5; // Maximum number of files that can be selected

    // Register activity result launcher for multiple file selection
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        // Handle multiple files
                        if (data.getClipData() != null) {
                            int count = Math.min(data.getClipData().getItemCount(), MAX_FILES);
                            for (int i = 0; i < count; i++) {
                                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                handleSelectedFile(fileUri);
                            }
                        }
                        // Handle single file
                        else if (data.getData() != null) {
                            handleSelectedFile(data.getData());
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddPostScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.browseButton.setOnClickListener(v -> openFilePicker());

        binding.uploadButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // Implement upload logic here
                uploadFiles();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // All file types
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple file selection
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            filePickerLauncher.launch(Intent.createChooser(intent, "Select Files"));
        } catch (android.content.ActivityNotFoundException ex) {
            showSnackbar("No file manager found on device");
        }
    }

    private void handleSelectedFile(Uri fileUri) {
        if (selectedFiles.size() >= MAX_FILES) {
            showSnackbar("Maximum " + MAX_FILES + " files allowed");
            return;
        }

        selectedFiles.add(fileUri);
        updateFileSelectionUI();
    }

    private void updateFileSelectionUI() {
        // Update UI to show selected files count
        String fileText = selectedFiles.size() + " file(s) selected";
        binding.browseButton.setText(fileText);
    }

    private void uploadFiles() {
        binding.uploadButton.setEnabled(false);

        binding.uploadButton.setEnabled(false);
        uploadedImageUrls.clear();

        for (Uri fileUri : selectedFiles) {
            String fileName = UUID.randomUUID().toString();
            StorageReference fileRef = storageReference.child("posts/" + fileName);

            fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadedImageUrls.add(uri.toString());
                        if (uploadedImageUrls.size() == selectedFiles.size()) {
                            addDataInModel();
                        }
                    })
            ).addOnFailureListener(e -> {
                binding.uploadButton.setEnabled(true);
                showSnackbar("Failed to upload image: " + e.getMessage());
            });
        }
    }

    private void addDataInModel() {
        String postId = db.collection("posts").document().getId(); //generate unique id

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.get("name").toString();
                    String profileImage = documentSnapshot.getString("profilePhotoUrl");

                    // Format the current date
                    String formattedDate = getFormattedDate();

                    PostModel postModel = new PostModel();
                    postModel.setPostId(postId);
                    postModel.setUserId(mAuth.getCurrentUser().getUid());
                    postModel.setPostTitle(binding.titleInput.getText().toString());
                    postModel.setPostDescription(binding.descriptionInput.getText().toString());
                    postModel.setImages(uploadedImageUrls);
                    postModel.setDate(formattedDate);
                    postModel.setLocation("Mumbai");
                    postModel.setStatus(true);
                    postModel.setLikeCount("0");
                    postModel.setUsername(userName);
                    postModel.setProfileImage(profileImage);

                    savePostToFirestore(postModel);

                });
    }

    private String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy h:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void savePostToFirestore(PostModel postModel) {

        db.collection("posts").document(postModel.getPostId())
                .set(postModel)
                .addOnSuccessListener(unused -> {
                    showSnackbar("Post uploaded successfully!");
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.uploadButton.setEnabled(true);
                    showSnackbar("Failed to upload post: " + e.getMessage());
                });
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate title
        if (binding.titleInput.getText().toString().trim().isEmpty()) {
            binding.titleLayout.setError("Please enter title");
            isValid = false;
        } else {
            binding.titleLayout.setError(null);
        }

        // Validate description
        if (binding.descriptionInput.getText().toString().trim().isEmpty()) {
            binding.descriptionLayout.setError("Please enter description");
            isValid = false;
        } else {
            binding.descriptionLayout.setError(null);
        }

        // Validate terms acceptance
        if (!binding.termsCheckbox.isChecked()) {
            showSnackbar("Please accept terms and conditions");
            isValid = false;
        }

        return isValid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        selectedFiles.clear();
    }
}