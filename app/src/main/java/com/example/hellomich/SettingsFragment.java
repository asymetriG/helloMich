package com.example.hellomich;


import static android.os.Build.VERSION_CODES.TIRAMISU;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hellomich.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.profileImageView.setImageURI(imageUri);
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User.getCurrentUser(new User.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    binding.usernameEditText.setText(user.getUsername().replace(user.getId(),""));
                    Glide.with(view).load(user.getProfilePictureUri()).into(binding.profileImageView);
                }


                if(Build.VERSION.SDK_INT>TIRAMISU) {
                    binding.changeProfilePictureButton.setOnClickListener(v -> {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        } else {
                            openImagePicker();
                        }
                    });
                } else {
                    binding.changeProfilePictureButton.setOnClickListener(v -> {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        } else {
                            openImagePicker();
                        }
                    });
                }



                binding.saveButton.setOnClickListener(v -> {
                    String newUsername = binding.usernameEditText.getText().toString() + user.getId();
                    if (!TextUtils.isEmpty(newUsername)) {
                        user.setUsername(newUsername);
                        firebaseFirestore.collection("users").document(user.getId())
                                .update("username", newUsername)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Username updated", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating username", Toast.LENGTH_SHORT).show());
                    }

                    if (imageUri != null) {
                        StorageReference profilePicRef = storageReference.child("profile_pictures/" + user.getId() + ".jpg");
                        profilePicRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    user.setProfilePictureUri(uri);
                                    firebaseFirestore.collection("users").document(user.getId())
                                            .update("profilePictureUri", uri.toString())
                                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating profile picture", Toast.LENGTH_SHORT).show());
                                }))
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading profile picture", Toast.LENGTH_SHORT).show());
                    }

                    String newPassword = binding.newPasswordEditText.getText().toString();
                    String confirmPassword = binding.confirmPasswordEditText.getText().toString();
                    if (!TextUtils.isEmpty(newPassword) && newPassword.equals(confirmPassword)) {
                        FirebaseUser fuser = firebaseAuth.getCurrentUser();
                        if (fuser != null) {
                            fuser.updatePassword(newPassword)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating password", Toast.LENGTH_SHORT).show());
                        }
                    } else if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void openImagePicker() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(pickPhoto);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
