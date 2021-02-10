package com.example.snapchatclone.RecyclerViewFollow;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.R;

class FollowViewHolders extends RecyclerView.ViewHolder {
    public TextView mName;
    public Button mFollow;

    public FollowViewHolders(@NonNull View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);

    }
}
