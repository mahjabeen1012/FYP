package com.childcareapp.pivak.fyplogin.Models;

public class UserEducation {
    String degree;
    String institution;
    String country;
    String city;
    String sDate;
    String eDate;
    public UserEducation()
    {

    }
    public UserEducation(String degree, String inst, String country, String city, String sDate, String eDate)
    {
        this.degree=degree;
        this.country=country;
        this.institution=inst;
        this.city=city;
        this.sDate=sDate;
        this.eDate=eDate;
    }

    public String getInstitution() {
        return institution;
    }

    public String getDegree() {
        return degree;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String geteDate() {
        return eDate;
    }

    public String getsDate() {
        return sDate;
    }
}
