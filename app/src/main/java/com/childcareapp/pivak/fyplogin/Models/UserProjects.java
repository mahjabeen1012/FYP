package com.childcareapp.pivak.fyplogin.Models;

import com.google.rpc.Help;

public class UserProjects {
    String title;
    String description;
    public  UserProjects(){}

    public UserProjects(String title, String description)
    {
        this.title=title;
        this.description=description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
