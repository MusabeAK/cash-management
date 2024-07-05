package org.pahappa.systems.requisitionapp.models;

import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class BudgetLine {
    private long id;
    private String title;
    private BudgetLineStatus budgetLineStatus;
    private int initialAmount;
    private Date startDate;
    private Date endDate;
    private BudgetLineStatus status;

    @OneToMany(mappedBy = "budgetLine")
    private List<Requisition> requisitions;

    @ManyToOne
    private BudgetLineCategory budgetLineCategory;

    public BudgetLine() {}

    private BudgetLine(String title, BudgetLineStatus budgetLineStatus, int initialAmount, Date startDate, Date endDate) {
        this.title = title;
        this.budgetLineStatus = budgetLineStatus;
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

    public BudgetLineStatus getBudgetLineStatus() {
        return budgetLineStatus;
    }

    public void setBudgetLineStatus(BudgetLineStatus budgetLineStatus) {
        this.budgetLineStatus = budgetLineStatus;
    }

    public int getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(int initialAmount) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BudgetLine that = (BudgetLine) o;
        return id == that.id && initialAmount == that.initialAmount && Objects.equals(title, that.title) && budgetLineStatus == that.budgetLineStatus && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, budgetLineStatus, initialAmount, startDate, endDate);
    }

    @Override
    public String toString() {
        return "BudgetLine{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", budgetLineStatus=" + budgetLineStatus +
                ", initialAmount=" + initialAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

}
