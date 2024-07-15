package com.example.hellomich;

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



    public void setRegisteredDate(Timestamp registeredDate) {
        this.registeredDate = registeredDate;
    }

    public User(String id, String username, String email, Timestamp registeredDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.registeredDate = registeredDate;
    }

    public static User findUser(String email) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final String[] email1 = new String[1];
        final String[] id = new String[1];
        final Timestamp[] registeredDate = new Timestamp[1];
        final String[] username = new String[1];
        ArrayList<User> users = new ArrayList<>();


        firebaseFirestore.collection("users").whereEqualTo("email", email).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(0);
                        email1[0] = (String) ds.get("email");
                        id[0] = (String) ds.get("id");
                        registeredDate[0] = (Timestamp) ds.get("registeredDate");
                        username[0] = (String) ds.get("username");

                    } else {
                        return;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error, possibly returning null

                    }
                });

        User u1 = new User(id[0], username[0],email, registeredDate[0]);
        return u1;
    }

    public void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

    public static void setCurrentUser(String email) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = findUser(email);
    }

    public static User getCurrentUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser!=null) {
            if (currentUser==null) {
                currentUser = findUser(firebaseUser.getEmail());
                return currentUser;
            } else {
                return currentUser;
            }
        } else {
            return null;
        }


    }


}