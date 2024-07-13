package com.example.hellomich;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hellomich.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private ArrayList<User> users = new ArrayList<>();;
    private ArrayList<Session> actives;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private User currentUser = User.getCurrentUser();
    private boolean isActivesSet = false;

    public interface MyCompletionListener {
        void onComplete();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actives = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void setActives(final MyCompletionListener listener) {
        firebaseFirestore.collection("sessions")
                .where(Filter.or(
                        Filter.equalTo("senderEmail", currentUser.getEmail()),
                        Filter.equalTo("receiverEmail", currentUser.getEmail())
                ))
                .whereEqualTo("isActive", true).get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                    }
                });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int[] completedTasks = {0};

        MyCompletionListener completionListener = new MyCompletionListener() {
            @Override
            public void onComplete() {
                completedTasks[0]++;
                if (completedTasks[0] == 2) {
                    // Initialize users array list and other components
                    //Toast.makeText(getContext(), "active size : " + actives.size(), Toast.LENGTH_SHORT).show();

                    userAdapter = new UserAdapter(users, actives);

                    String text = "Welcome, " + currentUser.getEmail();
                    binding.welcomeText.setText(text);

                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.recyclerView.setAdapter(userAdapter);
                }
            }
        };


        getData(completionListener);
        setActives(completionListener);

    }

    private void getData(final MyCompletionListener listener) {
        firebaseFirestore.collection("users").where(Filter.notEqualTo("email", currentUser.getEmail())).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
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
                        User user = new User(id, username, email, registeredDate);
                        users.add(user);
                    }
                    //userAdapter.notifyDataSetChanged();
                }

                // Call the listener when getData is complete
                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
