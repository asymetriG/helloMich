package com.example.hellomich;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellomich.databinding.RecyclerSessionRowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionHolder> {

    private ArrayList<Session> sessions;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    User currentUser = User.getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public class SessionHolder extends RecyclerView.ViewHolder {
        RecyclerSessionRowBinding binding;

        public SessionHolder(RecyclerSessionRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public SessionAdapter(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public SessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerSessionRowBinding binding = RecyclerSessionRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SessionHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionHolder holder, int position) {
        Session session = sessions.get(position);
        if(session.getSenderEmail().matches(currentUser.getEmail())) {
            holder.binding.recyclerViewSessionTextView.setText(session.getReceiverEmail());
        } else {
            holder.binding.recyclerViewSessionTextView.setText(session.getSenderEmail());
        }

        holder.binding.recyclerViewSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(),MapsActivity.class);
                intent.putExtra("showOldSession",true);
                intent.putExtra("createdAt", FieldValue.serverTimestamp().toString());
                intent.putExtra("receiverEmail",session.getReceiverEmail());
                intent.putExtra("senderEmail",session.getSenderEmail());
                intent.putExtra("receiverLang",session.getReceiverLang());
                intent.putExtra("receiverLong",session.getReceiverLong());
                intent.putExtra("senderLang",session.getSenderLang());
                intent.putExtra("senderLong",session.getSenderLong());

                holder.itemView.getContext().startActivity(intent);
            }
        });


        holder.binding.recyclerViewSessionButton.setBackgroundColor(Color.rgb(255,20,20));
        holder.binding.recyclerViewSessionButton.setText("View");


    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
