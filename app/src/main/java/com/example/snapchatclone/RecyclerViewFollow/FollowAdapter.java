package com.example.snapchatclone.RecyclerViewFollow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.R;
import com.example.snapchatclone.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders> {
    private List<FollowObject> usersList;
    private Context context;

    public FollowAdapter(List<FollowObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }
    @Override
    public FollowViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_followers_item, parent, false);
        FollowViewHolders rcv = new FollowViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolders holder, int position) {
        holder.mName.setText(usersList.get(position).getName());

        if(UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())){
            holder.mFollow.setText(R.string.Following);
        }else{
            holder.mFollow.setText(R.string.Follow);
        }

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(!UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())){
                    holder.mFollow.setText(R.string.Following);

                    DatabaseReference followRef = FirebaseDatabase.getInstance().getReference().child("users");
                    followRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String Uid = usersList.get(holder.getLayoutPosition()).getUid();

                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getUid());
                            Map userInfo = new HashMap<>();
                            userInfo.put("Uid", Uid);
                            userInfo.put("streak", "null");
                            userInfo.put("story", "null");
                            userInfo.put("msgId", "null");
                            currentUserDb.updateChildren(userInfo);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    String followUid = usersList.get(holder.getLayoutPosition()).getUid();
                    DatabaseReference friendOrNot = FirebaseDatabase.getInstance().getReference().child("users").child(followUid);
                    friendOrNot.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("following").exists()){
                                if(snapshot.child("following").child(userId).exists()){

                                }else{
                                    DatabaseReference addFriend = friendOrNot.child("addfriend").child(userId);
                                    Map userInfo = new HashMap<>();
                                    userInfo.put("Uid", userId);
                                    addFriend.updateChildren(userInfo);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }else{
                    holder.mFollow.setText(R.string.Follow);
                    //Remove from current user
                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following")
                            .child(usersList.get(holder.getLayoutPosition()).getUid()).removeValue();
                    //Remove from other user you following
                    FirebaseDatabase.getInstance().getReference().child("users").child(usersList.get(holder.getLayoutPosition()).getUid()).child("addfriend")
                            .child(userId).removeValue();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
