package com.childcareapp.pivak.fyplogin.Models;

public class UserAwards {
    String title;
    String year;
    String description;
    public UserAwards() { }
    public UserAwards(String title, String year,String description)
    {
        this.title=title;
        this.year=year;
        this.description=description;
    }

    public UserAwards(String title, String year) {
        this.title = title;
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }
}
