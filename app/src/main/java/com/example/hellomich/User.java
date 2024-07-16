package com.example.hellomich;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class User {
    private String id;
    private String username;
    private String email;
    private Timestamp registeredDate;
    private static User currentUser;
    private Uri profilePictureUri;

    public User(String id, String username, String email, Timestamp registeredDate,Uri profilePictureUri) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.registeredDate = registeredDate;
        this.profilePictureUri = profilePictureUri;
    }

    public interface OnUserFetchedListener {
        void onUserFetched(User user);
    }

    public static void findUser(String email, OnUserFetchedListener listener) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(0);
                        String id = (String) ds.get("id");
                        String username = (String) ds.get("username");
                        String email1 = (String) ds.get("email");
                        Timestamp registeredDate = (Timestamp) ds.get("registeredDate");
                        Uri profilePictureUri = Uri.parse((String) ds.get("profilePictureUri"));

                        User user = new User(id, username, email1, registeredDate, profilePictureUri);
                        listener.onUserFetched(user);
                    } else {
                        listener.onUserFetched(null); // Handle user not found
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    listener.onUserFetched(null);
                });
    }

    public static void setCurrentUser(String email, OnUserFetchedListener listener) {
        findUser(email, user -> {
            currentUser = user;
            listener.onUserFetched(user);
        });
    }

    public static void getCurrentUser(OnUserFetchedListener listener) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            if (currentUser == null) {
                setCurrentUser(firebaseUser.getEmail(), listener);
            } else {
                listener.onUserFetched(currentUser);
            }
        } else {
            listener.onUserFetched(null);
        }
    }



    public void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        try {
            firebaseAuth.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getRegisteredDate() {
        return registeredDate;
    }

    public Uri getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(Uri profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public void setRegisteredDate(Timestamp registeredDate) {
        this.registeredDate = registeredDate;
    }
}
