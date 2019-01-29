package com.childcareapp.pivak.fyplogin.Models;


public class CVData {

    private String institution;
    private String degree;
    private String duration;
    private String skills;
    private String title;
    private String workplace;
    private String description;


    public CVData (String duration, String title, String description, int i)
    {
        this.description = description;
        this.title = title;
        this.duration = duration;
    }

    public CVData (String title, String description)
    {
        this.description = description;
        this.title = title;
    }

    public CVData (String institution, String degree, String duration)
    {
        this.institution = institution;
        this.degree = degree;
        this.duration = duration;
    }

    public CVData (String duration, String title, String workplace, String description)
    {
        this.duration = duration;
        this.title = title;
        this.workplace = workplace;
        this.description = description;
    }

    public CVData (String skills)
    {
        this.skills = skills;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getDegree() {
        return degree;
    }

    public String getDuration() {
        return duration;
    }

    public String getInstitution() {
        return institution;
    }

    public String getSkills() {
        return skills;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getWorkplace() {
        return workplace;
    }
}
