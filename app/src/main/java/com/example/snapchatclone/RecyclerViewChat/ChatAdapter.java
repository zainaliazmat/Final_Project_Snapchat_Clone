package com.example.snapchatclone.RecyclerViewChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapchatclone.DisplayImageActivity;
import com.example.snapchatclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatAdapter extends FirebaseRecyclerAdapter <ChatObject, ChatAdapter.ViewHolder>{


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param context
     */
    Boolean streak;
    String userId;
    Context mContext;
    public ChatAdapter(@NonNull FirebaseRecyclerOptions<ChatObject> options, Context context) {
        super(options);
        mContext = context;
        streak = false;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position, @NonNull ChatObject model) {
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(model.getUid());
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("name").exists()){
                        holder.mUserName.setText(snapshot.child("name").getValue().toString().trim());
                    }
                    if(snapshot.child("profileImageUrl").exists()){
                        if(!snapshot.child("profileImageUrl").getValue().toString().trim().equals("default")){
                            try {
                                Glide.with(mContext)
                                        .load(snapshot.child("profileImageUrl").getValue().toString().trim())
                                        .fitCenter()
                                        .into(holder.mFollowerProfile);
                            }catch (Exception ignored){}
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       userId = model.getUid();
        DatabaseReference received = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        received.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("received").exists()){
                    if(snapshot.child("received").child(model.getUid()).exists()){
                        holder.mNewStreak.setVisibility(View.VISIBLE);
                        streak = true;
                    } else{
                        holder.mNewStreak.setVisibility(View.GONE);
                        streak = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_chat_item, parent, false);
        return new ViewHolder(layoutView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mUserName;
        public LinearLayout mLayout;
        public LinearLayout mNewStreak;
        public ImageView mFollowerProfile;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.userChat);
            mLayout = itemView.findViewById(R.id.chatlayout);
            mNewStreak = itemView.findViewById(R.id.newStreak);
            mFollowerProfile = itemView.findViewById(R.id.followerProfile);
            mNewStreak.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(streak){
                        Intent intent = new Intent(view.getContext(), DisplayImageActivity.class);
                        Bundle b = new Bundle();
                        b.putString("userId", userId);
                        b.putString("chatOrStory", "chat");
                        intent.putExtras(b);
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}

