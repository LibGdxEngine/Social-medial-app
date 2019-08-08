package com.libgdx.ahmed_fathy.socialapp.Models;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content , userID , userImage , userName;
    private Object tiemstamp;

    public Comment() {
    }

    public Comment(String content, String userID, String userImage, String userName) {
        this.content = content;
        this.userID = userID;
        this.userImage = userImage;
        this.userName = userName;
        this.tiemstamp = ServerValue.TIMESTAMP;
    }

    public Comment(String content, String userID, String userImage, String userName, Object tiemstamp) {
        this.content = content;
        this.userID = userID;
        this.userImage = userImage;
        this.userName = userName;
        this.tiemstamp = tiemstamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getTiemstamp() {
        return tiemstamp;
    }

    public void setTiemstamp(Object tiemstamp) {
        this.tiemstamp = tiemstamp;
    }
}
