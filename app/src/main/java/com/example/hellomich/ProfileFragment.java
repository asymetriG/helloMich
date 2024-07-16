package com.example.hellomich;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hellomich.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private FragmentProfileBinding binding;
    private SessionAdapter sessionAdapter;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final ArrayList<Session> sessions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface MyProfileCompletionListener {
        void onComplete();
    }

    private void getData(final MyProfileCompletionListener listener) {
        User.getCurrentUser(new User.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                firebaseFirestore.collection("oldSessions")
                        .whereEqualTo("isActive",false)
                        .where(Filter.or(Filter.equalTo("senderEmail", user.getEmail()), Filter.equalTo("receiverEmail", user.getEmail())))
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> data = ds.getData();
                                Timestamp createdAt = (Timestamp) data.get("createdAt");
                                boolean isActive = (boolean) data.get("isActive");
                                String senderEmail = (String) data.get("senderEmail");
                                String receiverEmail = (String) data.get("receiverEmail");
                                double receiverLong = (double) data.get("receiverLong");
                                double receiverLang = (double) data.get("receiverLang");
                                double senderLong = (double) data.get("senderLong");
                                double senderLang = (double) data.get("senderLang");
                                Session session = new Session(senderEmail, receiverEmail, createdAt, isActive, senderLang, receiverLang, senderLong, receiverLong);
                                sessions.add(session);
                            }
                            if (listener != null) {
                                listener.onComplete();
                            }
                        });
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int[] completedTasks = {0};
        MyProfileCompletionListener myProfileCompletionListener = () -> {
            completedTasks[0]++;
            if (completedTasks[0] == 1) {
                binding.sessionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                sessionAdapter = new SessionAdapter(sessions);
                binding.sessionRecyclerView.setAdapter(sessionAdapter);

                binding.deleteAccountButton.setBackgroundColor(Color.RED);
                binding.deleteAccountButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User.getCurrentUser(new User.OnUserFetchedListener() {
                            @Override
                            public void onUserFetched(User user) {

                                firebaseFirestore.collection("users").document(user.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(getContext(),"User Deleted",Toast.LENGTH_SHORT).show();
                                            firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task2) {
                                                    if(task2.isSuccessful()) {
                                                        Intent intent = new Intent(getContext(),MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(getContext(),"Not Redirecting",Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(getContext(),task2.getException() + "",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(getContext(),"User Could Not Deleted From Collections",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        });
                    }
                });

                binding.logoutButton.setOnClickListener(v -> {
                    try {
                        User.getCurrentUser(new User.OnUserFetchedListener() {
                            @Override
                            public void onUserFetched(User user) {
                                if (user != null) {
                                    user.logout();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }
                        });

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        getData(myProfileCompletionListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
