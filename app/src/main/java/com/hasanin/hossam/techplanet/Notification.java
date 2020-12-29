package com.hasanin.hossam.techplanet;

import com.google.firebase.Timestamp;

public class Notification {

    String title;
    String body;
    String from;
    String to;
    String collection;
    Timestamp timestamp;

    public Notification(){}

    public Notification(String title, String body, String from, String to, String collection, Timestamp timestamp) {
        this.title = title;
        this.body = body;
        this.from = from;
        this.to = to;
        this.collection = collection;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
