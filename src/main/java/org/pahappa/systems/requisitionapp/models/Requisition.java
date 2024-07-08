package org.pahappa.systems.requisitionapp.models;

import java.util.Date;

public class Requisition {
    private int id;
    private String subject;
    private String description;
    private Date dateNeeded;

    public Requisition() {}

    private Requisition(int id, String subject, String description, Date dateNeeded) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.dateNeeded = dateNeeded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateNeeded() {
        return dateNeeded;
    }

    public void setDateNeeded(Date dateNeeded) {
        this.dateNeeded = dateNeeded;
    }


}

