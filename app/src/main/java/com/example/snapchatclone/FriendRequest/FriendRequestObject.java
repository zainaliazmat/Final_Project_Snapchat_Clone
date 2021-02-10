package com.example.snapchatclone.FriendRequest;

public class FriendRequestObject {
    private String Uid;

    public FriendRequestObject() {
    }

    public FriendRequestObject(String uid) {
        Uid = uid;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
