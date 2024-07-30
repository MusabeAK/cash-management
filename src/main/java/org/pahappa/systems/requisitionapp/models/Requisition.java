package org.pahappa.systems.requisitionapp.models;

import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "requisitions")
public class Requisition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requisition_id")
    private int id;

    @Column(name = "subject")
    private String subject;

    @Column(name = "comment")
    private String comment;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private int amount;

    @Column(name = "date_needed")
    private Date dateNeeded;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequisitionStatus status;

    @Column(name = "draft_timestamp")
    private Date draftTimestamp;

    @Column(name = "hr_reviewed_timestamp")
    private Date hrReviewedTimestamp;

    @Column(name = "ceo_approved_timestamp")
    private Date ceoApprovedTimestamp;

    @Column(name = "rejected_timestamp")
    private Date rejectedTimestamp;

    @Column(name = "disbursed_timestamp")
    private Date disbursedTimestamp;

    @Column(name = "submitted_timestamp")
    private Date submittedTimestamp;

    @ManyToOne
    @JoinColumn(name = "budget_line_id")
    private BudgetLine budgetLine;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
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
        Date now = new Date();
        switch (status) {
            case DRAFT:
                this.draftTimestamp = now;
                break;
            case HR_REVIEWED:
                this.hrReviewedTimestamp = now;
                break;
            case CEO_APPROVED:
                this.ceoApprovedTimestamp = now;
                break;
            case REJECTED:
                this.rejectedTimestamp = now;
                break;
            case DISBURSED:
                this.disbursedTimestamp = now;
                break;
            case SUBMITTED:
                this.submittedTimestamp = now;
                break;
        }
    }

    public Date getStatusTimestamp(RequisitionStatus status) {
        switch (status) {
            case DRAFT:
                return draftTimestamp;
            case HR_REVIEWED:
                return hrReviewedTimestamp;
            case CEO_APPROVED:
                return ceoApprovedTimestamp;
            case REJECTED:
                return rejectedTimestamp;
            case DISBURSED:
                return disbursedTimestamp;
            case SUBMITTED:
                return submittedTimestamp;
            default:
                return null;
        }
    }

    public Date getDraftTimestamp() {
        return draftTimestamp;
    }

    public void setDraftTimestamp(Date draftTimestamp) {
        this.draftTimestamp = draftTimestamp;
    }

    public Date getHrReviewedTimestamp() {
        return hrReviewedTimestamp;
    }

    public void setHrReviewedTimestamp(Date hrReviewedTimestamp) {
        this.hrReviewedTimestamp = hrReviewedTimestamp;
    }

    public Date getCeoApprovedTimestamp() {
        return ceoApprovedTimestamp;
    }

    public void setCeoApprovedTimestamp(Date ceoApprovedTimestamp) {
        this.ceoApprovedTimestamp = ceoApprovedTimestamp;
    }

    public Date getRejectedTimestamp() {
        return rejectedTimestamp;
    }

    public void setRejectedTimestamp(Date rejectedTimestamp) {
        this.rejectedTimestamp = rejectedTimestamp;
    }

    public Date getDisbursedTimestamp() {
        return disbursedTimestamp;
    }

    public void setDisbursedTimestamp(Date disbursedTimestamp) {
        this.disbursedTimestamp = disbursedTimestamp;
    }

    public Date getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(Date submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

