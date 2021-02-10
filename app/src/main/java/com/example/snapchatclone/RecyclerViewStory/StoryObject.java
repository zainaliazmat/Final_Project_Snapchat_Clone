package com.example.snapchatclone.RecyclerViewStory;

public class StoryObject {
    private String Uid;
    private String story;
    private String streak;

    public StoryObject() {
    }

    public StoryObject(String uid, String story, String streak) {
        Uid = uid;
        this.story = story;
        this.streak = streak;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getStreak() {
        return streak;
    }

    public void setStreak(String streak) {
        this.streak = streak;
    }
}
