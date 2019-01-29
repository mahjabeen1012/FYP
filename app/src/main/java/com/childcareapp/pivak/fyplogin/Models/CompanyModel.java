package com.childcareapp.pivak.fyplogin.Models;

import java.net.URL;

public class CompanyModel {
    //String userName;
    String name;
    String country, city, zipCode;
    String contact,email;
    String facebook,twiter,linkedIn,website;
    String status;
    //URL image;

    public CompanyModel()
    {

    }
    public CompanyModel( String name, String country,String city, String zipCode, String contact,String email, String status,
                    String facebook, String twiter, String linkedIn, String website)
    {
        this.name=name;
        this.country=country;
        this.city=city;
        this.zipCode=zipCode;
        this.contact=contact;
        this.email=email;
        this.status=status;
        this.facebook=facebook;
        this.twiter=twiter;
        this.linkedIn=linkedIn;
        this.website=website;
    }

//   public CompanyModel(String userID,String name,URL url)
//    {
//        this.userName=userID;
//        this.name=name;
//        this.image=url;
//
//    }
    public String getFacebook() {
        return facebook;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public String getTwiter() {
        return twiter;
    }

    public String getWebsite() {
        return website;
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

    public String getName() {
        return name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

}
