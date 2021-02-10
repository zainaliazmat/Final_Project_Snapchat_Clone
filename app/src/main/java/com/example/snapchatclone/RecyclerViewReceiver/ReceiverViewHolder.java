package com.example.snapchatclone.RecyclerViewReceiver;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snapchatclone.R;

class ReceiverHolders extends RecyclerView.ViewHolder {
    public TextView mEmail;
    public Button mReceive;

    public ReceiverHolders(@NonNull View itemView) {
        super(itemView);
        mEmail = itemView.findViewById(R.id.receiveEmail);
        mReceive = itemView.findViewById(R.id.receive);

    }
}
