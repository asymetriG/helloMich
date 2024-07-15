package com.example.hellomich;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hellomich.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String NOT_AUTH = "The supplied auth credential is incorrect, malformed or has expired.";
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User currentUser = User.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (currentUser != null) {
            firebaseAuth.signOut();
            //Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
        }
    }

    public void registerTextClick(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void loginUser(View view) {
        String emailText = binding.emailInputText.getText().toString();
        String passwordText = binding.passwordInputText.getText().toString();

        if (emailText.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (passwordText.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (emailText.length() < 8) {
            Toast.makeText(this, "Email must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else if (passwordText.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnFailureListener(e -> {
                        if (e.getLocalizedMessage().matches(NOT_AUTH)) {
                            Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(authResult -> {
                        User.setCurrentUser(firebaseAuth.getCurrentUser().getEmail());
                        Toast.makeText(MainActivity.this, "Logged in as: " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
        }
    }
}
