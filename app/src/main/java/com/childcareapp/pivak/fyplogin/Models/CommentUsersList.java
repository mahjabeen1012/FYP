package com.childcareapp.pivak.fyplogin.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CommentUsersList
{
    String id,comment,postId;
    @ServerTimestamp Date timeStamp;
    long timeInMillis;

    public CommentUsersList(){}
    public CommentUsersList(String id, String comment, Date timeStamp, long timeInMillis,String postId)
    {
        this.id=id;
        this.comment=comment;
        this.timeInMillis=timeInMillis;
        this.timeStamp=timeStamp;
        this.postId = postId;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
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
}