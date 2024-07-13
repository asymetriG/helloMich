package com.example.hellomich;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hellomich.databinding.ActivityMainBinding;
import com.example.hellomich.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String notAuth = "The email address is already in use by another account.";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore db;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null){
            System.out.println(" ");
        }


    }

    public void loginTextClick(View view) {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void registerUser(View view) {
        String emailText = binding.emailInputText.getText().toString();
        String passwordText = binding.passwordInputText.getText().toString();
        String confirmText = binding.passwordConfirmInputText.getText().toString();
        String usernameText = binding.usernameInputText.getText().toString();

        if (emailText.isEmpty()) {

            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (usernameText.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (usernameText.length()<8) {
            Toast.makeText(this, "Username must be at least 8 characters", Toast.LENGTH_SHORT).show();
        }

        else if (passwordText.isEmpty()) {

            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (confirmText.isEmpty()) {

            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
        } else if (emailText.length() < 8) {

            Toast.makeText(this, "Email must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else if (passwordText.length() < 8) {

            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else if (!passwordText.equals(confirmText)) {

            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText).addOnFailureListener(e -> {
                if (e.getLocalizedMessage().matches(notAuth)) {
                    Toast.makeText(this,"This email is in use.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(authResult -> {

                Map<String, Object> user = new HashMap<>();
                user.put("email", emailText);
                user.put("registeredDate", FieldValue.serverTimestamp());
                user.put("id",authResult.getUser().getUid());
                user.put("username",usernameText+authResult.getUser().getUid());

                db.collection("users").add(user).addOnSuccessListener(documentReference -> {
                    if (firebaseAuth.getCurrentUser().getEmail()!=null) {
                        Toast.makeText(RegisterActivity.this,"Registered as : " + firebaseAuth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                });
            });
        }

    }
}