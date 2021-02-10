package com.example.snapchatclone.RecyclerViewChat;

import com.example.snapchatclone.MyApplication;

import java.util.ArrayList;

public class ChatObject {
    private String Uid;
    private String msgId;

    public ChatObject() {
    }

    public ChatObject(String uid, String msgId) {
        Uid = uid;
        this.msgId = msgId;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}