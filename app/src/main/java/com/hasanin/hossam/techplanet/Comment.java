package com.hasanin.hossam.techplanet;


import com.google.firebase.Timestamp;

public class Comment {

    String userId;
    String content;
    Timestamp timestamp;

    public Comment(){}

    public Comment(String userId, String content, Timestamp timestamp) {
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
