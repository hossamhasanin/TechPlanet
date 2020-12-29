package com.hasanin.hossam.techplanet;

import com.google.firebase.Timestamp;

public class Posts {

    String userId;
    String postId;
    String image;
    String title;
    String description;
    Timestamp timestamp;

    public Posts(){}

    public Posts(String userId, String postId, String image, String title, String description, Timestamp timestamp) {
        this.userId = userId;
        this.postId = postId;
        this.image = image;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostID(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
