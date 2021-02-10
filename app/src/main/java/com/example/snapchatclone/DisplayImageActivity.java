package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.snapchatclone.RecyclerViewStory.StoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class DisplayImageActivity extends AppCompatActivity {

    String userId, chatOrStory;
    private ImageView mImage;
    private boolean started = false;
    ProgressBar imageProgressbar;
    ProgressBar loadingProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imageProgressbar = findViewById(R.id.progressBar);
        imageProgressbar.setVisibility(View.GONE);

        loadingProgressbar = findViewById(R.id.loadingProgress);
        loadingProgressbar.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();
        userId = b.getString("userId");
        chatOrStory = b.getString("chatOrStory");

        mImage = findViewById(R.id.imageView);

        switch(chatOrStory){
            case "chat":
                listenForChat();
                break;
            case "story":
                listenForStory();
        }
    }

    private void listenForChat() {
        DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("received").child(userId);
        chatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageURL = "";
                for(DataSnapshot chatSnapshot : snapshot.getChildren()){
                    if(chatSnapshot.child("imageURL").getValue() != null){
                        imageURL =chatSnapshot.child("imageURL").getValue().toString();
                    }
                    imageUrlList.add(imageURL);
                    if(!started){
                        started = true;
                        initializeDisplay();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ArrayList<String> imageUrlList = new ArrayList<>();
    private void listenForStory() {
        DatabaseReference followingStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        followingStoryDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageURL = "";
                long timestampBeg = 0;
                long timestampEnd = 0;
                for(DataSnapshot storySnapshot : snapshot.child("story").getChildren()){
                    if(storySnapshot.child("timeStampBeg").getValue() != null){
                        timestampBeg = Long.parseLong(storySnapshot.child("timeStampBeg").getValue().toString());
                    }
                    if(storySnapshot.child("timeStampEnd").getValue() != null){
                        timestampEnd = Long.parseLong(storySnapshot.child("timeStampEnd").getValue().toString());
                    }
                    if(storySnapshot.child("imageURL").getValue() != null){
                        imageURL =storySnapshot.child("imageURL").getValue().toString();
                    }
                    long timestampCurrent = System.currentTimeMillis();
                    if(timestampCurrent >= timestampBeg && timestampCurrent <= timestampEnd){
                        imageUrlList.add(imageURL);
                        if(!started){
                            started = true;
                            initializeDisplay();
                        }
                    }else{
                        followingStoryDb.child(storySnapshot.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int imageIterator = 0;
    final int[] progress = {0};
    private void initializeDisplay() {
        RequestBuilder<Drawable> listener = Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Toast.makeText(DisplayImageActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                imageProgressbar.setVisibility(View.VISIBLE);
                imageProgressbar.setProgress(progress[0]=0);
                return false;
            }
        });

        listener.into(mImage);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });
        final Handler handler = new Handler();
        final int delay = 10000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeImage();
                handler.postDelayed(this,delay);
            }
        }, delay);

        final Handler progressHandler = new Handler();
        final int progressDelay = 100;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageProgressbar.setProgress(progress[0]+=1);
                progressHandler.postDelayed(this,progressDelay);
            }
        }, progressDelay);
    }

    private void changeImage() {
        if(imageIterator == imageUrlList.size() - 1){
            if(chatOrStory.equals("chat")){
                deleteChat();
            }
            finish();
            return;
        }
        imageIterator++;

        RequestBuilder<Drawable> listener = Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Toast.makeText(DisplayImageActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                imageProgressbar.setVisibility(View.VISIBLE);
                imageProgressbar.setProgress(progress[0]=0);
                return false;
            }
        });

        listener.into(mImage);
    }

    private void deleteChat(){
        DatabaseReference firebase =  FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        firebase.child("received").child(userId).removeValue();
        firebase.child("following").child(userId).child("streak").setValue("null");
    }
}