package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.example.snapchatclone.RecyclerViewReceiver.ReceiverAdapter;
import com.example.snapchatclone.RecyclerViewReceiver.ReceiverObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseReceiverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    String uid;
    Bitmap bitmap;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        try {
            bitmap = BitmapFactory.decodeStream(getApplication().openFileInput("imageToSend"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getUid();

        mRecyclerView = findViewById(R.id.sendRecyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new ReceiverAdapter(getDataSet(), getApplication());
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveToFirebase();
            }
        });
    }

    private final ArrayList<ReceiverObject> results = new ArrayList<>();
    private ArrayList<ReceiverObject> getDataSet() {
        listenForData();
        return results;
    }

    private void listenForData() {
        for(int i = 0; i < UserInformation.listFollowing.size(); i++) {
            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
            userDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = "";
                    String uid = snapshot.getRef().getKey();
                    if(snapshot.child("email").getValue() != null){
                        email = snapshot.child("email").getValue().toString();
                    }
                    ReceiverObject obj = new ReceiverObject(email, uid, false);
                    if(!results.contains(obj)){
                        results.add(obj);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void SaveToFirebase() {
        final DatabaseReference userStoryDB = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("story");
        final String Key = userStoryDB.push().getKey();

        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("captures").child(Key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
        byte[] dataToUpload = baos.toByteArray();
        UploadTask uploadTask = filepath.putBytes(dataToUpload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Long CurrentTimeStamp = System.currentTimeMillis();
                        Long EndTimeStamp = CurrentTimeStamp + (24*60*60*1000);

                        // uploading Stories to Firebase Database...
                        CheckBox mStory = findViewById(R.id.story);
                        if(mStory.isChecked()){
                            Map<String, Object> mapToUpload = new HashMap<>();
                            mapToUpload.put("imageURL", downloadUrl.toString());
                            mapToUpload.put("timeStampBeg", CurrentTimeStamp);
                            mapToUpload.put("timeStampEnd", EndTimeStamp);
                            userStoryDB.child(Key).setValue(mapToUpload);
                        }

                        //uploading Streaks to Firebase Database....
                        for(int i = 0; i < results.size(); i++){
                            if(results.get(i).getReceive()){
                                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("users").child(results.get(i).getUid()).child("received").child(uid);
                                Map<String, Object> mapToUpload = new HashMap<>();
                                mapToUpload.put("imageURL", downloadUrl.toString());
                                mapToUpload.put("timeStampBeg", CurrentTimeStamp);
                                mapToUpload.put("timeStampEnd", EndTimeStamp);
                                userDb.child(Key).setValue(mapToUpload);
                            }
                        }
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
            }
        });

    }
}