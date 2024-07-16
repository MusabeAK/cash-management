package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;
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
import java.util.ArrayList;
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
    private List<BudgetLine> approvedBudgetLines;
    private List<BudgetLine> draftBudgetLines;
    private BudgetLine selectedBudgetLine;

    @PostConstruct
    public void init() {
        newBudgetLineCategory = new BudgetLineCategory();
        budgetLineCategories = budgetLineCategoryService.getAllBudgetLineCategories();
        newBudgetLine = new BudgetLine();
        budgetLines = budgetLineService.getAllBudgetLines();
        approvedBudgetLines = new ArrayList<>();
        for (BudgetLine budgetLine : budgetLines) {
            if (budgetLine.getStatus() == BudgetLineStatus.APPROVED) {
                approvedBudgetLines.add(budgetLine);
            }
        }
        for (BudgetLine budgetLine : budgetLines) {
            if (budgetLine.getStatus() == BudgetLineStatus.DRAFT) {
                draftBudgetLines.add(budgetLine);
            }
        }
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
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.CREATE_BUDGET_LINE)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        try {
            BudgetLineCategory budgetLineCategory = budgetLineCategoryService.getBudgetLineCategoryByName(budgetLineCategoryName);
            if (budgetLineCategory != null) {
                budgetLineService.createBudgetLine(newBudgetLine, budgetLineCategory);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
                newBudgetLine = new BudgetLine();
                newBudgetLineCategory = new BudgetLineCategory();
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
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.EDIT_BUDGET_LINE)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        try {
            if (!selectedBudgetLine.getStatus().equals(BudgetLineStatus.DRAFT)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot update a budget line that is not in draft", null));
                return;
            }
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            loadBudgetLines();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    /*
    to-do
    add a check so that budget line cannot be deleted if there exist any requisitions where accountability has not been provided (done)
    add approval of budget lines (done)
    add cron job to expire budget line
    add pool to collect expired budget line amounts
     */

    public void approveBudgetLine() {
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.APPROVE_BUDGET_LINE)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedBudgetLine.getStatus().equals(BudgetLineStatus.DRAFT)) {
            selectedBudgetLine.setStatus(BudgetLineStatus.APPROVED);
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Budget Line approved", null));
            approvedBudgetLines.add(selectedBudgetLine);
        } else
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Budget Line already approved", null));
    }

    public void rejectBudgetLine() {
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.REJECT_BUDGET_LINE)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedBudgetLine.getStatus().equals(BudgetLineStatus.DRAFT)) {
            selectedBudgetLine.setStatus(BudgetLineStatus.REJECTED);
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Budget Line rejected", null));
        } else
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Budget Line already approved", null));
    }

    public void deleteBudgetLine(BudgetLine budgetLine) {
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.DELETE_BUDGET_LINE)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        try {
            List<Requisition> budgetLineRequisitions = budgetLine.getRequisitions();
            for (Requisition requisition : budgetLineRequisitions){
                if (requisition.getStatus().equals(RequisitionStatus.DISBURSED) && requisition.getAccountability() == null){
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete a budget line with a requisition which has not yet been provided accountability for.", null));
                    return;
                }
            }
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
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.CREATE_BUDGET_LINE_CATEGORY)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
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
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.EDIT_BUDGET_LINE_CATEGORY)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
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
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.DELETE_BUDGET_LINE_CATEGORY)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        try {
            List<BudgetLine> budgetLineCategoryBudgetLines = category.getBudgetLines();
            for (BudgetLine budgetLineCategoryBudgetLine : budgetLineCategoryBudgetLines){
                if (budgetLineCategoryBudgetLine.getStatus().equals(BudgetLineStatus.APPROVED)){
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete a budget line with a requisition which has not yet been provided accountability for.", null));
                    return;
                }
            }
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
        return budgetLineService.getAllBudgetLines();
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

    public List<BudgetLine> getApprovedBudgetLines() {
        return approvedBudgetLines;
    }

    public void setApprovedBudgetLines(List<BudgetLine> approvedBudgetLines) {
        this.approvedBudgetLines = approvedBudgetLines;
    }

    public List<BudgetLine> getDraftBudgetLines() {
        return draftBudgetLines;
    }

    public void setDraftBudgetLines(List<BudgetLine> draftBudgetLines) {
        this.draftBudgetLines = draftBudgetLines;
    }
}