package com.example.snapchatclone.FriendRequest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.snapchatclone.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestAdapter extends FirebaseRecyclerAdapter<FriendRequestObject, FriendRequestAdapter.ViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context mContext;
    public FriendRequestAdapter(@NonNull FirebaseRecyclerOptions<FriendRequestObject> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendRequestAdapter.ViewHolder holder, int position, @NonNull FriendRequestObject model) {
        DatabaseReference addFriendDb = FirebaseDatabase.getInstance().getReference().child("users").child(model.getUid());
        addFriendDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.mName.setText(snapshot.child("name").getValue().toString().trim());
                try {
                    String url = snapshot.child("profileImageUrl").getValue().toString().trim();
                    if(!url.equals("default")){
                        RequestBuilder<Drawable> listener = Glide.with(mContext).load(url).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        });
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mFollow.setText(R.string.Following);
                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following")
                        .child(model.getUid());
                Map userInfo = new HashMap<>();
                userInfo.put("Uid", model.getUid());
                userInfo.put("streak", "null");
                userInfo.put("story", "null");
                userInfo.put("msgId", "null");
                currentUserDb.updateChildren(userInfo);

                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("addfriend")
                        .child(model.getUid()).removeValue();
            }
        });


    }

    @NonNull
    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_followers_item, parent, false);
        return new ViewHolder(layoutView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public Button mFollow;
        public ImageView friendPicture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.email);
            mFollow = itemView.findViewById(R.id.follow);
            friendPicture = itemView.findViewById(R.id.friendPicture);
        }
    }
}
