package org.pahappa.systems.requisitionapp.models;

import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "budget_lines")
public class BudgetLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_line_id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "initial_amount")
    private double initialAmount;

    @Column(name = "balance")
    private double balance;

    @Column(name = "float_amount")
    private double floatAmount;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "comment")
    private String comment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BudgetLineStatus status;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "budgetLine")
    private List<Requisition> requisitions;

    @ManyToOne
    @JoinColumn(name = "budget_line_category_id")
    private BudgetLineCategory budgetLineCategory;

    public BudgetLine() {}

    private BudgetLine(String title, BudgetLineStatus status, double initialAmount, Date startDate, Date endDate) {
        this.title = title;
        this.status = status;
        this.initialAmount = initialAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BudgetLineStatus getStatus() {
        return status;
    }

    public void setStatus(BudgetLineStatus status) {
        this.status = status;
    }

    public BudgetLineCategory getBudgetLineCategory() {
        return budgetLineCategory;
    }

    public void setBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        this.budgetLineCategory = budgetLineCategory;
    }

    public List<Requisition> getRequisitions() {
        return requisitions;
    }

    public void setRequisitions(List<Requisition> requisitions) {
        this.requisitions = requisitions;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getFloatAmount() {
        return floatAmount;
    }

    public void setFloatAmount(double floatAmount) {
        this.floatAmount = floatAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BudgetLine that = (BudgetLine) o;
        return id == that.id && initialAmount == that.initialAmount && Objects.equals(title, that.title) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && status == that.status && Objects.equals(budgetLineCategory, that.budgetLineCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, initialAmount, startDate, endDate, status, budgetLineCategory);
    }

    @Override
    public String toString() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
