package com.example.hellomich;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellomich.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private final ArrayList<User> users;
    private final ArrayList<Session> actives;


    public UserAdapter(ArrayList<User> users, ArrayList<Session> actives) {
        this.users = users;
        this.actives = actives;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserHolder(recyclerRowBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        String username = users.get(position).getUsername().replace(users.get(position).getId(), "");
        holder.recyclerRowBinding.recyclerViewTextView.setText(username);
        holder.recyclerRowBinding.recyclerViewTextView.setTextColor(Color.rgb(0, 0, 0));

        boolean isSessioned = false;

        for (Session session : actives) {
            if (users.get(position).getEmail().matches(session.getSenderEmail()) || users.get(position).getEmail().matches(session.getReceiverEmail())) {
                isSessioned = true;
                holder.recyclerRowBinding.recyclerViewButton.setText("Ongoing Session");
                holder.recyclerRowBinding.recyclerViewButton.setBackgroundColor(Color.rgb(250, 25, 25));
                holder.recyclerRowBinding.recyclerViewButton.setTextColor(Color.rgb(250, 255, 255));
                holder.recyclerRowBinding.recyclerViewTextView.setTextColor(Color.rgb(255, 255, 255));

                holder.recyclerRowBinding.recyclerViewButton.setOnClickListener(view -> {
                    User.getCurrentUser(new User.OnUserFetchedListener() {
                        @Override
                        public void onUserFetched(User user) {
                            Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                            intent.putExtra("isActiveSession", true);
                            intent.putExtra("senderEmail", session.getSenderEmail());
                            intent.putExtra("receiverEmail", session.getReceiverEmail());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });

                });
                break;
            }
        }

        if (!isSessioned) {
            holder.recyclerRowBinding.recyclerViewButton.setOnClickListener(view -> {
                User.getCurrentUser(new User.OnUserFetchedListener() {
                    @Override
                    public void onUserFetched(User user) {
                        Toast.makeText(holder.itemView.getContext(),"Sıfır Request",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                        intent.putExtra("newRequest",true);
                        intent.putExtra("isActiveSession", false);
                        intent.putExtra("senderEmail", user.getEmail());
                        intent.putExtra("receiverEmail", users.get(position).getEmail());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        RecyclerRowBinding recyclerRowBinding;

        public UserHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }
}
