package com.example.snapchatclone.RecyclerViewFollow;

public class FollowObject {
    private String name;
    private String uid;

    public FollowObject(String email, String uid) {
        this.name = email;
        this.uid = uid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
}
