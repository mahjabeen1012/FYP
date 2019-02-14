package com.childcareapp.pivak.fyplogin.Models;

import java.net.URL;

public class UserModel {
    String userId;
    String fName;
    String lName;
    String country;
    String city;
    String zipCode;
    String contact;
    String email;
    String status;
    String skills;
    String softwares;
    String headline;
    String currentPosition;
    String degree;
    String batch;
    String discipline;
    String campus;
    URL image;

    public UserModel() { }
    public UserModel( String fName, String lName, String country,String city, String zipCode, String contact,String email, String status,
                    String skills, String softwares,String headline, String currentPosition,String degree, String batch,
                    String discipline,String campus)
    {
        this.fName=fName;
        this.lName=lName;
        this.country=country;
        this.city=city;
        this.zipCode=zipCode;
        this.contact=contact;
        this.email=email;
        this.status=status;
        this.skills=skills;
        this.softwares=softwares;
        this.headline=headline;
        this.currentPosition=currentPosition;
        this.degree=degree;
        this.discipline=discipline;
        this.batch=batch;
        this.campus=campus;
    }

    public UserModel(URL image, String fName, String lName, String userID)
    {
        this.image=image;
        this.fName=fName;
        this.lName=lName;
        this.userId=userID;
    }
    public UserModel(String image, String fName, String userID)
    {
        this.lName=image;
        this.fName=fName;
        this.userId=userID;
    }
    public UserModel(String skills, String softwares){
        this.skills=skills;
        this.softwares=softwares;
    }

    public URL getImage() {
        return image;
    }

    public String getUserId() {
        return userId;
    }

    public String getDegree() {
        return degree;
    }

    public String getBatch() {
        return batch;
    }

    public String getCampus() {
        return campus;
    }

    public String getDiscipline() {
        return discipline;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public String getHeadline() {
        return headline;
    }

    public String getStatus() {
        return status;
    }

    public String getContact() {
        return contact;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getSkills() {
        return skills;
    }

    public String getSoftwares() {
        return softwares;
    }

}
