package com.example.hellomich;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellomich.databinding.RecyclerSessionRowBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionHolder> {

    private final ArrayList<Session> sessions;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final User currentUser = User.getCurrentUser();

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
        holder.binding.recyclerViewSessionButton.setTextColor(Color.rgb(255, 255, 255));
        Session session = sessions.get(position);
        holder.binding.recyclerViewSessionTextView.setText(session.getSenderEmail().matches(currentUser.getEmail()) ?
                session.getReceiverEmail() : session.getSenderEmail());

        holder.binding.recyclerViewSessionButton.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
            intent.putExtra("showOldSession", true);
            intent.putExtra("createdAt", session.getCreatedAt().toString());
            intent.putExtra("receiverEmail", session.getReceiverEmail());
            intent.putExtra("senderEmail", session.getSenderEmail());
            intent.putExtra("receiverLang", session.getReceiverLang());
            intent.putExtra("receiverLong", session.getReceiverLong());
            intent.putExtra("senderLang", session.getSenderLang());
            intent.putExtra("senderLong", session.getSenderLong());

            holder.itemView.getContext().startActivity(intent);
        });

        holder.binding.recyclerViewSessionButton.setBackgroundColor(Color.rgb(255, 20, 20));
        holder.binding.recyclerViewSessionButton.setText("View");
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
