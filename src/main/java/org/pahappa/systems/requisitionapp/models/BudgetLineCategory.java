package org.pahappa.systems.requisitionapp.models;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

public class BudgetLineCategory {
    private int id;
    private String categoryName;

    @OneToMany(mappedBy = "budgetLine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BudgetLine> budgetLines;

    public BudgetLineCategory() {}

    private BudgetLineCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<BudgetLine> getBudgetLines() {
        return budgetLines;
    }

    public void setBudgetLines(List<BudgetLine> budgetLines) {
        this.budgetLines = budgetLines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BudgetLineCategory that = (BudgetLineCategory) o;
        return id == that.id && Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoryName);
    }

    @Override
    public String toString() {
        return "BudgetLineCategory{" +
                "categoryName='" + categoryName + '\'' +
                '}';
    }
}
