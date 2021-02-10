package com.example.snapchatclone.RecyclerViewStory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snapchatclone.DisplayImageActivity;
import com.example.snapchatclone.R;
import com.example.snapchatclone.RecyclerViewChat.ChatObject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends FirebaseRecyclerAdapter<StoryObject, StoryAdapter.ViewHolder>{

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private Context mContext;
    private String userId;

    public StoryAdapter(@NonNull FirebaseRecyclerOptions<StoryObject> options , Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull StoryAdapter.ViewHolder holder, int position, @NonNull StoryObject model) {
        if(model.getUid() != null){
            userId = model.getUid();
            DatabaseReference storyDB = FirebaseDatabase.getInstance().getReference().child("users").child(model.getUid());
            storyDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child("name").exists() && snapshot.child("name") != null){
                            holder.mStory.setText(snapshot.child("name").getValue().toString().trim());
                        }
                        if(snapshot.child("story").exists()){
                            holder.mViewedOrNot.setVisibility(View.VISIBLE);
                            String url = "";
                            if(snapshot.child("story").getChildrenCount() != 0){
                                for(DataSnapshot storySnapshot : snapshot.child("story").getChildren()){
                                    url = storySnapshot.child("imageURL").getValue().toString().trim();
                                }
                                try {
                                    Glide.with(mContext).load(url).centerCrop().placeholder(R.drawable.snapchat).into(holder.mImageView);
                                }catch (Exception e){ }
                            }
                        } else{
                            holder.mViewedOrNot.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_story_item, parent, false);
        return new ViewHolder(layoutView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mStory;
        public FrameLayout mLayout;
        public ImageView mImageView;
        public CardView mViewedOrNot;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStory = itemView.findViewById(R.id.userStory);
            mLayout = itemView.findViewById(R.id.layout);
            mImageView = itemView.findViewById(R.id.storyImage);
            mViewedOrNot = itemView.findViewById(R.id.viewedOrNot);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userId != null){
                        mViewedOrNot.setCardBackgroundColor(null);
                        Intent intent = new Intent(view.getContext(), DisplayImageActivity.class);
                        Bundle b = new Bundle();
                        b.putString("userId", userId);
                        b.putString("chatOrStory", "story");
                        intent.putExtras(b);
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }
    }
}