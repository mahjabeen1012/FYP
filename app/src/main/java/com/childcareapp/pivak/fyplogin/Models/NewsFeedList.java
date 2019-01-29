package com.childcareapp.pivak.fyplogin.Models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.net.URL;
import java.util.Date;

@IgnoreExtraProperties
public class NewsFeedList {
    String downloadUrl, uploaderId, uploaderName,content,filetype,fileName;
    private @ServerTimestamp Date timestamp;
    URL Img;


    public NewsFeedList(String postContent, String uName, String downloadUrl, String uploaderName, String fileType, Date timeStamp,
                        URL Img,String fileName)
    {
        this.content = postContent;
        this.downloadUrl = downloadUrl;
        this.uploaderId = uName;
        this.uploaderName = uploaderName;
        this.filetype = fileType;
        this.timestamp =  timeStamp;
        this.Img = Img;
        this.fileName = fileName;
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
    public void setImg(URL bitmap) { this.Img = bitmap; }
    public void setFileName (String fileName) { this.fileName = fileName; }

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
    public URL getImg() {return Img;}
    public String getFileName() { return fileName; }
}
