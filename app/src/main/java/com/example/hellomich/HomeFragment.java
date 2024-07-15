package com.example.hellomich;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hellomich.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Filter;

import java.util.ArrayList;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private UserAdapter userAdapter;
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Session> actives = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    public interface MyCompletionListener {
        void onComplete();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    public void setActives(final MyCompletionListener listener) {
        User.getCurrentUser(user -> {

            firebaseFirestore.collection("sessions")
                    .where(Filter.or(
                            Filter.equalTo("senderEmail", user.getEmail()),
                            Filter.equalTo("receiverEmail", user.getEmail())
                    ))
                    .whereEqualTo("isActive", true).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot qds : task.getResult()) {
                                Map<String, Object> data = qds.getData();
                                String senderEmail = (String) data.get("senderEmail");
                                String receiverEmail = (String) data.get("receiverEmail");
                                Timestamp createdAt = (Timestamp) data.get("createdAt");
                                double senderLang = (double) data.get("senderLang");
                                double receiverLang = (double) data.get("receiverLang");
                                double senderLong = (double) data.get("senderLong");
                                double receiverLong = (double) data.get("receiverLong");
                                Session session = new Session(senderEmail, receiverEmail, createdAt, true, senderLang, receiverLang, senderLong, receiverLong);
                                actives.add(session);
                            }
                        }

                        // Call the listener when setActives is complete
                        if (listener != null) {
                            listener.onComplete();
                        }
                    });
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int[] completedTasks = {0};

        MyCompletionListener completionListener = () -> {
            completedTasks[0]++;
            if (completedTasks[0] == 2) {
                User.getCurrentUser(new User.OnUserFetchedListener() {
                    @Override
                    public void onUserFetched(User user) {
                        userAdapter = new UserAdapter(users, actives);
                        String text = "Welcome, " + user.getEmail();
                        binding.welcomeText.setText(text);
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.recyclerView.setAdapter(userAdapter);
                    }
                });

            }
        };

        getData(completionListener);
        setActives(completionListener);
    }

    private void getData(final MyCompletionListener listener) {
        User.getCurrentUser(new User.OnUserFetchedListener() {
            @Override
            public void onUserFetched(User user) {
                firebaseFirestore.collection("users")
                        .where(Filter.notEqualTo("email", user.getEmail()))
                        .addSnapshotListener((value, error) -> {
                            if (error != null) {
                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (value != null) {
                                users.clear();
                                for (DocumentSnapshot ds : value.getDocuments()) {
                                    Map<String, Object> data = ds.getData();
                                    String email = (String) data.get("email");
                                    String username = (String) data.get("username");
                                    String id = (String) data.get("id");
                                    Timestamp registeredDate = (Timestamp) data.get("registeredDate");
                                    Uri profilePictureUri = Uri.parse((String) ds.get("profilePictureUri"));
                                    User nuser = new User(id, username, email, registeredDate,profilePictureUri);
                                    users.add(nuser);
                                }
                            }

                            // Call the listener when getData is complete
                            if (listener != null) {
                                listener.onComplete();
                            }
                        });
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
