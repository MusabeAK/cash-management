package org.pahappa.systems.requisitionapp.models;

import java.util.Date;

public class Review {
    private long id;
    private String comment;
    private Date reviewDate;

    public Review(){}

    private Review(String comment, Date reviewDate) {
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

}
