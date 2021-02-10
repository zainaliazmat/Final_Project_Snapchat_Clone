package com.example.snapchatclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.snapchatclone.RecyclerViewChat.ChatAdapter;
import com.example.snapchatclone.RecyclerViewChat.ChatObject;
import com.example.snapchatclone.RecyclerViewStory.StoryAdapter;
import com.example.snapchatclone.RecyclerViewStory.StoryObject;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class StoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private StoryAdapter mStoryAdopter;
    private RecyclerView.LayoutManager mLayoutManager;

    ImageView currentUserStory;
    TextView currentUserName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static StoryFragment newInstance(){
        StoryFragment fragment = new StoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);


        FirebaseRecyclerOptions<StoryObject> options = new FirebaseRecyclerOptions.Builder<StoryObject>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child(getString(R.string.users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getString(R.string.following)), StoryObject.class)
                .build();

        mStoryAdopter = new StoryAdapter (options, getContext());
        mRecyclerView.setAdapter(mStoryAdopter);

        currentUserName = view.findViewById(R.id.currentUserName);
        currentUserStory = view.findViewById(R.id.currentUserStoryView);
        currentUserStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DisplayImageActivity.class);
                Bundle b = new Bundle();
                b.putString(getString(R.string.userId), FirebaseAuth.getInstance().getCurrentUser().getUid());
                b.putString(getString(R.string.chatOrStory), getString(R.string.story));
                intent.putExtras(b);
                getContext().startActivity(intent);
            }
        });
        getCurrentUserStory();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mStoryAdopter.startListening();
    }

    private void getCurrentUserStory() {
        DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        followingStoryDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName.setText(snapshot.child(getString(R.string.name)).getValue().toString());
                String imageURL = "";
                long timestampBeg = 0;
                long timestampEnd = 0;
                if(snapshot.child(getString(R.string.story)).exists()){
                    for(DataSnapshot storySnapshot : snapshot.child(getString(R.string.story)).getChildren()){
                        if(storySnapshot.child(getString(R.string.timeStampBeg)).exists() && storySnapshot.child(getString(R.string.timeStampBeg)).getValue() != null){
                            timestampBeg = Long.parseLong(storySnapshot.child(getString(R.string.timeStampBeg)).getValue().toString());
                        }
                        if(storySnapshot.child(getString(R.string.timeStampEnd)).exists() && storySnapshot.child(getString(R.string.timeStampEnd)).getValue() != null){
                            timestampEnd = Long.parseLong(storySnapshot.child(getString(R.string.timeStampEnd)).getValue().toString());
                        }
                        if(storySnapshot.child(getString(R.string.imageURL)).exists() && storySnapshot.child(getString(R.string.imageURL)).getValue() != null){
                            imageURL =storySnapshot.child(getString(R.string.imageURL)).getValue().toString();
                        }
                        long timestampCurrent = System.currentTimeMillis();
                        if(timestampBeg != 0 && timestampEnd != 0){
                            if(timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd){
                                try {
                                    Glide.with(getActivity()).load(imageURL).centerCrop().placeholder(R.drawable.snapchat).into(currentUserStory);
                                }catch (Exception e){ }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}