package com.example.krishiyog;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.krishiyog.databinding.ActivitySignupScreenBinding;
import com.example.krishiyog.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignupScreen extends AppCompatActivity {

    ActivitySignupScreenBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignupScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseManager.getInstance().getAuth();
        db = FirebaseManager.getInstance().getFirestore();

        //logInBtn clickListener
        binding.loginBtn.setOnClickListener(view -> changeActivity(new Intent(SignupScreen.this, LoginScreen.class)));

        //SignUp Btn SendOtp
        binding.signBtn.setOnClickListener( view -> createUser());

    }

    private void createUser() {
        String phoneNo = binding.phoneNo.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        boolean isValidated = validateData(email, password, phoneNo);
        if (!isValidated) {
            return;
        }

        //creating user in firebase
        createAccountUsingFirebase("name", email, password, phoneNo);
    }

    //Method to create a user in firebase
    private void createAccountUsingFirebase(String name, String email, String password, String phoneNumber) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //changeInProgress(false);
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        // Get the user ID
                        String userId = currentUser.getUid();
                        // Create the User object with provided details
                        User user = new User(userId, name, email, phoneNumber, "", false, null); // profilePhotoUrl is empty for now
                        storeUsersDetail(user);

                        // Create an Intent to start the new activity
                        Intent intent = new Intent(SignupScreen.this, HomeScreen.class);
                        // Optionally pass user data with the intent
                        intent.putExtra("userEmail", email);
                        intent.putExtra("userPhone", phoneNumber);
                        // Start the new activity
                        startActivity(intent);
                        // Optionally, finish the current activity
                        finishAffinity();
                       // showAlertDialog();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method to store the user detail in Database
    private void storeUsersDetail(User user) {
        db.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnFailureListener(aVoid ->{
                    Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                });
    }

    //Method to validate the data
    boolean validateData(String email, String password, String phoneNo) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("Email is Invalid");
            return false;
        }
        if (password.length() < 6) {
            binding.password.setError("Password length should be greater than 6");
            return false;
        }
        if (phoneNo.length() < 10) {
            binding.phoneNo.setError("Mobile No is Invalid");
            return false;
        }
        return true;
    }


    //Method to change activity
    private void changeActivity(Intent i){
        startActivity(i);
    }

}