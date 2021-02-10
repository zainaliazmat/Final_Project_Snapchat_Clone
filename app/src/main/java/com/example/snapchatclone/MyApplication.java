package com.example.snapchatclone;

import android.app.Application;

import com.example.snapchatclone.RecyclerViewChat.ChatObject;
import com.example.snapchatclone.RecyclerViewStory.StoryObject;

import java.util.ArrayList;

public class MyApplication extends Application {
    public static ArrayList<StoryObject> stories;
    public static ArrayList<ChatObject> friends;

    @Override
    public void onCreate() {
        super.onCreate();
        stories = new ArrayList<>();
        friends = new ArrayList<>();
    }
}
