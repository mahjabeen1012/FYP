package com.childcareapp.pivak.fyplogin.Models;

public class UserExperience {
    String designation;
    String organization;
    String country;
    String city;
    String sDate;
    String eDate;
    String description;
    public UserExperience()
    {

    }
    public UserExperience(String designation, String org, String country, String city, String sDate, String eDate, String description)
    {
        this.designation=designation;
        this.organization=org;
        this.country=country;
        this.city=city;
        this.sDate=sDate;
        this.eDate=eDate;
        this.description=description;
    }
    public UserExperience(String designation, String org, String country, String city, String sDate, String eDate)
    {
        this.designation=designation;
        this.organization=org;
        this.country=country;
        this.city=city;
        this.sDate=sDate;
        this.eDate=eDate;
    }

    public String getDescription() {
        return description;
    }

    public String getsDate() {
        return sDate;
    }

    public String getDesignation() {
        return designation;
    }

    public String geteDate() {
        return eDate;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getOrganization() {
        return organization;
    }
}
