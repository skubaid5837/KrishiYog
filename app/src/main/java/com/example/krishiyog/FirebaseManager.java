package com.example.krishiyog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseFirestore firestore;
    private FirebaseAuth Auth;

    private FirebaseManager() {
        //Initialize Firebase Services
        firestore = FirebaseFirestore.getInstance();
        Auth = FirebaseAuth.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public FirebaseAuth getAuth() {
        return Auth;
    }
}
