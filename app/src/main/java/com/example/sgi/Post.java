package com.example.sgi;

import android.annotation.SuppressLint;

import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;

public class Post {
    private String username;
    private String postKey;
    private String description;
    private String picture;
    private String userId;
    private String userPhoto;

    private Object timeStamp;

    Post(String description, String picture, String userId, String username, String userPhoto) {
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.username = username;
        this.userPhoto = userPhoto;
        long date = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm a" );
        this.timeStamp = sdf.format(date);

    }

    public Post(String username, String description, String userId, String userPhoto) {
        this.username = username;

        this.description = description;
        this.userId = userId;
        this.userPhoto = userPhoto;
        long date = System.currentTimeMillis();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm a" );
        this.timeStamp = sdf.format(date);

    }

    public Post() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
