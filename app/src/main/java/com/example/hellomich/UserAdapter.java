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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private ArrayList<User> users;
    private ArrayList<Session> actives;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private User currentUser = User.getCurrentUser();
    private boolean isSenderUser;




    public UserAdapter(ArrayList<User> users,ArrayList<Session> actives) {
        this.users = users;
        this.actives = actives;
    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserHolder(recyclerRowBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        String username = users.get(position).getUsername().replace(users.get(position).getId(),"");
        holder.recyclerRowBinding.recyclerViewTextView.setText(username);
        String test = actives.size()+" test";
        boolean isSessioned = false;
        holder.recyclerRowBinding.recyclerViewTextView.setTextColor(Color.rgb(0,0,0));

        for (Session session : actives) {

            if(users.get(position).getEmail().matches(session.getSenderEmail())) {
                isSessioned = true;
                holder.recyclerRowBinding.recyclerViewButton.setText("Ongoing Session");
                holder.recyclerRowBinding.recyclerViewButton.setBackgroundColor(Color.rgb(250,25,25));
                holder.recyclerRowBinding.recyclerViewButton.setTextColor(Color.rgb(250,255,255));
                holder.recyclerRowBinding.recyclerViewTextView.setTextColor(Color.rgb(255,255,255));


                holder.recyclerRowBinding.recyclerViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(holder.itemView.getContext(),MapsActivity.class);
                        intent.putExtra("isActiveSession",true);
                        intent.putExtra("senderEmail",users.get(position).getEmail());
                        intent.putExtra("receiverEmail",currentUser.getEmail());
                        intent.putExtra("againJoin",false);


                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            } else if (users.get(position).getEmail().matches(session.getReceiverEmail())) {
                isSessioned = true;
                holder.recyclerRowBinding.recyclerViewButton.setText("Ongoing Session");
                holder.recyclerRowBinding.recyclerViewButton.setBackgroundColor(Color.rgb(250,25,25));
                holder.recyclerRowBinding.recyclerViewButton.setTextColor(Color.rgb(250,255,255));
                holder.recyclerRowBinding.recyclerViewTextView.setTextColor(Color.rgb(255,255,255));
                holder.recyclerRowBinding.recyclerViewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(holder.itemView.getContext(),MapsActivity.class);

                        intent.putExtra("isActiveSession",true);
                        intent.putExtra("senderEmail",currentUser.getEmail());
                        intent.putExtra("receiverEmail",users.get(position).getEmail());
                        intent.putExtra("againJoin",true);
                        Toast.makeText(holder.itemView.getContext(),"Again Join",Toast.LENGTH_SHORT).show();

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        holder.itemView.getContext().startActivity(intent);

                    }
                });
            }
        }

        if(!isSessioned) {

            holder.recyclerRowBinding.recyclerViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.itemView.getContext(),MapsActivity.class);
                    intent.putExtra("isActiveSession",false);
                    intent.putExtra("senderEmail",currentUser.getEmail());
                    intent.putExtra("receiverEmail",users.get(position).getEmail());

                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        RecyclerRowBinding recyclerRowBinding;

        public UserHolder(RecyclerRowBinding recyclerRowBinding) {

            super(recyclerRowBinding.getRoot());

            this.recyclerRowBinding = recyclerRowBinding;
        }

    }
}