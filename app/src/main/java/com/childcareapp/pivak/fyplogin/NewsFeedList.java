package com.childcareapp.pivak.fyplogin;

import android.graphics.Bitmap;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.net.URL;
import java.util.Date;

@IgnoreExtraProperties
public class NewsFeedList {
    String downloadUrl, uploaderId, uploaderName,content,filetype,fileName,postId;
    @ServerTimestamp Date timestamp;
    Integer likes;
    long timeInMillis;

    public NewsFeedList(String postContent, String uName, String downloadUrl, String uploaderName, String fileType, Date timeStamp,String fileName,String postId,Integer likes,long timeInMillis)
    {
        this.content = postContent;
        this.downloadUrl = downloadUrl;
        this.uploaderId = uName;
        this.uploaderName = uploaderName;
        this.filetype = fileType;
        this.timestamp =  timeStamp;
        this.fileName = fileName;
        this.postId = postId;
        this.likes = likes;
        this.timeInMillis = timeInMillis;
    }
    public NewsFeedList(){}

    public void setContent(String content)
    {
        this.content = content;
    }
    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
    public void setUploaderName(String uploaderName)
    {
        this.uploaderName = uploaderName;
    }
    public void setFiletype(String filetype)
    {
        this.filetype = filetype;
    }
    public void setUploaderId(String uploaderId)
    {
        this.uploaderId = uploaderId;
    }
    public void setTimeStamp(Date timeStamp)
    {
        this.timestamp = timeStamp;
    }
    public void setFileName (String fileName) { this.fileName = fileName; }
    public void setPostId(String postId)
    {
        this.postId = postId;
    }
    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getContent()
    {
        return content;
    }
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
    public String getUploaderName()
    {
        return uploaderName;
    }
    public String getUploaderId()
    {
        return uploaderId;
    }
    public String getFiletype()
    {
        return filetype;
    }
    public Date getTimeStamp()
    {
        return timestamp;
    }
    public String getFileName() { return fileName; }
    public String getPostId() {return postId; }
    public Integer getLikes() {
        return likes;
    }
    public long getTimeInMillis() {
        return timeInMillis;
    }
}