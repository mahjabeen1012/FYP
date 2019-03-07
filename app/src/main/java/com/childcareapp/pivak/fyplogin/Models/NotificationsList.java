package com.childcareapp.pivak.fyplogin.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotificationsList
{
    String id,notification,postId,status,uploaderId;

    @ServerTimestamp Date timeStamp;
    long timeInMillis;
    String url;

    public NotificationsList(){}
    public NotificationsList(String id, String notification, Date timeStamp, long timeInMillis,String postId,String status,String uploaderId)
    {
        this.id=id;
        this.status=status;
        this.timeInMillis=timeInMillis;
        this.timeStamp=timeStamp;
        this.postId = postId;
        this.notification = notification;
        this.uploaderId = uploaderId;
    }
    public NotificationsList(String id, String notification, String status, long timeInMillis, String url, String postID, String uID)
    {
        this.id=id;
        this.status=status;
        this.timeInMillis=timeInMillis;
        this.notification = notification;
        this.url=url;
        this.postId=postID;
        this.uploaderId=uID;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public String getPostId() {
        return postId;
    }

    public String getNotification() {
        return notification;
    }

    public String getUploaderId() {
        return uploaderId;
    }
}