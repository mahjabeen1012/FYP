package com.childcareapp.pivak.fyplogin.Models;

public class Images {
    String name;
    String url;
    public Images()
    {

    }
    public Images(String name, String url)
    {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        this.name=name;
        this.url=url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
