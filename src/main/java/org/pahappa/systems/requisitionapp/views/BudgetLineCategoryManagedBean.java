package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.services.BudgetLineCategoryService;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
public class BudgetLineCategoryManagedBean implements Serializable {

    @Autowired
    private BudgetLineCategoryService budgetLineCategoryService;

    @Autowired
    private BudgetLineService budgetLineService;

    private BudgetLineCategory newBudgetLineCategory;
    private List<BudgetLineCategory> budgetLineCategories;
    private String budgetLineCategoryName;
    private BudgetLineCategory selectedBudgetLineCategory;

    private BudgetLine newBudgetLine;
    private List<BudgetLine> budgetLines;
    private BudgetLine selectedBudgetLine;

    @PostConstruct
    public void init() {
        newBudgetLineCategory = new BudgetLineCategory();
        budgetLineCategories = budgetLineCategoryService.getAllBudgetLineCategories();
        newBudgetLine = new BudgetLine();
        budgetLines = budgetLineService.getAllBudgetLines();
    }

    public void selectBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        this.selectedBudgetLineCategory = budgetLineCategory;
    }

    public void selectBudgetLine(BudgetLine budgetLine) {
        if (budgetLine.getStatus().equals(BudgetLineStatus.DRAFT)) {
            this.selectedBudgetLine = budgetLine;
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot edit a budget line that is not in draft.", null));
        }
    }

    public void addBudgetLineToBudgetLineCategory() {
        BudgetLineCategory budgetLineCategory = budgetLineCategoryService.getBudgetLineCategoryByName(budgetLineCategoryName);
        try {
            if (budgetLineCategory != null) {
                budgetLineService.createBudgetLine(newBudgetLine, budgetLineCategory);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
                newBudgetLine = new BudgetLine();
                loadBudgetLines();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Budget Line with that category name does not exist.", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void updateBudgetLine() {
        try {
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            loadBudgetLines();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void deleteBudgetLine(BudgetLine budgetLine) {
        try {
            budgetLineService.deleteBudgetLine(budgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            loadBudgetLines();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void loadBudgetLines(){
        try {
            budgetLines = budgetLineService.getAllBudgetLines();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void loadBudgetLineCategories() {
        try {
            budgetLineCategories = budgetLineCategoryService.getAllBudgetLineCategories();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void addBudgetLineCategory() {
        try {
            budgetLineCategoryService.createBudgetLineCategory(newBudgetLineCategory);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            newBudgetLineCategory = new BudgetLineCategory();
            loadBudgetLineCategories();
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void updateBudgetLineCategory() {
        try {
            budgetLineCategoryService.updateBudgetLineCategory(selectedBudgetLineCategory);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            loadBudgetLineCategories();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void deleteBudgetLineCategory(BudgetLineCategory category) {
        try {
            budgetLineCategoryService.deleteBudgetLineCategory(category);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Budget Line Category deleted successfully"));
            loadBudgetLineCategories();
            loadBudgetLines();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public List<BudgetLineCategory> budgetLineCategoryList(String query) {
        return budgetLineCategoryService.searchBudgetLineCategoriesByName(query);
    }

    // Getters and setters
    public BudgetLineCategory getNewBudgetLineCategory() {
        return newBudgetLineCategory;
    }

    public void setNewBudgetLineCategory(BudgetLineCategory newBudgetLineCategory) {
        this.newBudgetLineCategory = newBudgetLineCategory;
    }

    public List<BudgetLineCategory> getBudgetLineCategories() {
        return budgetLineCategories;
    }

    public void setBudgetLineCategories(List<BudgetLineCategory> budgetLineCategories) {
        this.budgetLineCategories = budgetLineCategories;
    }

    public String getBudgetLineCategoryName() {
        return budgetLineCategoryName;
    }

    public void setBudgetLineCategoryName(String budgetLineCategoryName) {
        this.budgetLineCategoryName = budgetLineCategoryName;
    }

    public BudgetLine getNewBudgetLine() {
        return newBudgetLine;
    }

    public void setNewBudgetLine(BudgetLine newBudgetLine) {
        this.newBudgetLine = newBudgetLine;
    }

    public List<BudgetLine> getBudgetLines() {
        return budgetLines;
    }

    public void setBudgetLines(List<BudgetLine> budgetLines) {
        this.budgetLines = budgetLines;
    }

    public BudgetLineCategory getSelectedBudgetLineCategory() {
        return selectedBudgetLineCategory;
    }

    public void setSelectedBudgetLineCategory(BudgetLineCategory selectedBudgetLineCategory) {
        this.selectedBudgetLineCategory = selectedBudgetLineCategory;
    }

    public BudgetLine getSelectedBudgetLine() {
        return selectedBudgetLine;
    }

    public void setSelectedBudgetLine(BudgetLine selectedBudgetLine) {
        this.selectedBudgetLine = selectedBudgetLine;
    }
}