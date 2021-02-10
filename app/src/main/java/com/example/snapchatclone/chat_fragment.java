package com.example.snapchatclone;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.snapchatclone.RecyclerViewChat.ChatAdapter;
import com.example.snapchatclone.RecyclerViewChat.ChatObject;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class chat_fragment extends Fragment{

    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static chat_fragment newInstance(){
        chat_fragment fragment = new chat_fragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider_line));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        FirebaseRecyclerOptions<ChatObject> options = new FirebaseRecyclerOptions.Builder<ChatObject>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("following") , ChatObject.class)
                .build();
        mChatAdapter = new ChatAdapter(options, getContext());
        mRecyclerView.setAdapter(mChatAdapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mChatAdapter.startListening();
    }
}