package com.childcareapp.pivak.fyplogin.Models;

import android.graphics.Bitmap;

import com.google.firebase.firestore.ServerTimestamp;

import java.net.URL;
import java.util.Date;

public class ChatDataModel {
    String name;
    String image;
    String message;
    String status;
    String readStatus;
    @ServerTimestamp
    Date timestamp;
    ChatDataModel()
    {
    }
    public ChatDataModel(String name, String message, String status,String readStatus, String image)
    {
        this.name = name;
        this.message = message;
        this.status=status;
        this.readStatus=readStatus;
        this.image=image;
    }

//    public ChatDataModel(String image, String name, String lastText,String readStatus, String time)
//    {
//        this.image = image;
//        this.name = name;
//        this.message = lastText;
//        this.readStatus=readStatus;
//        this.status=time;
//    }

    public ChatDataModel(String msgType, String msgContent, String image) {
        this.status = msgType;
        this.message = msgContent;
        this.image=image;
    }

    public String getImage() {
        return image;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
