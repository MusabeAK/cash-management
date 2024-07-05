package org.pahappa.systems.requisitionapp.models;

import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;
import java.util.Objects;

public class Requisition {
    private int id;
    private String subject;
    private String description;
    private int amount;
    private Date dateNeeded;
    private RequisitionStatus status;

    @ManyToOne
    private BudgetLine budgetLine;

    @ManyToOne
    private User user;

    @OneToOne
    private Review review;

    @OneToOne
    private Accountability accountability;

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public RequisitionStatus getStatus() {
        return status;
    }

    public void setStatus(RequisitionStatus status) {
        this.status = status;
    }

    public BudgetLine getBudgetLine() {
        return budgetLine;
    }

    public void setBudgetLine(BudgetLine budgetLine) {
        this.budgetLine = budgetLine;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public Accountability getAccountability() {
        return accountability;
    }

    public void setAccountability(Accountability accountability) {
        this.accountability = accountability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requisition that = (Requisition) o;
        return id == that.id && Objects.equals(subject, that.subject) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subject, description);
    }
}

