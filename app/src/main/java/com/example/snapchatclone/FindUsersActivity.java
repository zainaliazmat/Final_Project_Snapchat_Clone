package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.snapchatclone.FriendRequest.FriendRequestAdapter;
import com.example.snapchatclone.FriendRequest.FriendRequestObject;
import com.example.snapchatclone.RecyclerViewChat.ChatObject;
import com.example.snapchatclone.RecyclerViewFollow.FollowAdapter;
import com.example.snapchatclone.RecyclerViewFollow.FollowObject;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.example.snapchatclone.R.drawable.recyclerview_divider_line;

public class FindUsersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView, mFRRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private FriendRequestAdapter mFRAdapter;
    private RecyclerView.LayoutManager mLayoutManager, mFRLayoutManager;

    EditText mInput;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mInput = findViewById(R.id.input);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplication(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(recyclerview_divider_line));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new FollowAdapter(getDataSet(), getApplication());
        mRecyclerView.setAdapter(mAdapter);


        mFRRecyclerView = findViewById(R.id.friendRequestRV);
        mFRRecyclerView.setNestedScrollingEnabled(false);
        mFRRecyclerView.setHasFixedSize(true);
        mFRLayoutManager = new LinearLayoutManager(getApplication());
        mFRRecyclerView.setLayoutManager(mFRLayoutManager);
        DividerItemDecoration dividerItem = new DividerItemDecoration(getApplication(), DividerItemDecoration.VERTICAL);
        dividerItem.setDrawable(getResources().getDrawable(recyclerview_divider_line));
        mFRRecyclerView.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<FriendRequestObject> options = new FirebaseRecyclerOptions.Builder<FriendRequestObject>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("addfriend") , FriendRequestObject.class)
                .build();

        mFRAdapter = new FriendRequestAdapter(options, getApplication());
        mFRRecyclerView.setAdapter(mFRAdapter);



        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                clear();
                listenForData();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFRAdapter.startListening();
    }

    private void listenForData() {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = userDb.orderByChild("name").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String name = "";
                String uid = snapshot.getRef().getKey();
                if(snapshot.child("name").getValue() != null){
                    name = snapshot.child("name").getValue().toString();
                }
                if(!uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    FollowObject obj = new FollowObject(name, uid);
                    results.add(obj);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0,size);
    }

    private final ArrayList<FollowObject> results = new ArrayList<>();
    private ArrayList<FollowObject> getDataSet() {
        listenForData();
        return results;
    }

    public void closeActivity(View view) {
        finish();
        return;
    }
}