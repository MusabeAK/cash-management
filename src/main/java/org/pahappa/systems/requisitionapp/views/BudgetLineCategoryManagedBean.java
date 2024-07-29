package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;
import org.pahappa.systems.requisitionapp.services.BudgetLineCategoryService;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.pahappa.systems.requisitionapp.views.utils.NewChartBean;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ViewScoped
public class BudgetLineCategoryManagedBean implements Serializable {

    @Autowired
    private BudgetLineCategoryService budgetLineCategoryService;

    @Autowired
    private BudgetLineService budgetLineService;

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private NewChartBean newChartBean;

    private BudgetLineCategory newBudgetLineCategory;
    private List<BudgetLineCategory> budgetLineCategories;
    private String budgetLineCategoryName;
    private BudgetLineCategory selectedBudgetLineCategory;
    private BudgetLineCategory selectedCategory;
    private List<BudgetLineCategory> filteredBudgetLineCategories;

    private List<String> budgetLineStatuses;
    private BudgetLineStatus selectedStatus;
    private String searchQuery;
    private String categorySearchQuery;

    private BudgetLine newBudgetLine;
    private List<BudgetLine> budgetLines;
    private List<BudgetLine> filteredBudgetLines;
    private List<BudgetLine> approvedBudgetLines;
    private List<BudgetLine> draftBudgetLines;
    private BudgetLine selectedBudgetLine;
    private int activeBudgetLineCount;
    private double budgetLineSummedTotal;

    private String currentForm;

    @PostConstruct
    public void init() {
        activeBudgetLineCount = budgetLineService.getActiveBudgetLineCount();
        newBudgetLineCategory = new BudgetLineCategory();

        Set<BudgetLineCategory> uniqueCategories = new HashSet<>(budgetLineCategoryService.getAllBudgetLineCategories());
        budgetLineCategories = new ArrayList<>(uniqueCategories);
        filteredBudgetLineCategories = new ArrayList<>(uniqueCategories);

        budgetLines = budgetLineService.getAllBudgetLines();
        filteredBudgetLines = new ArrayList<>(budgetLines);

        budgetLineStatuses = Arrays.stream(BudgetLineStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        newBudgetLine = new BudgetLine();
        draftBudgetLines = new ArrayList<>();
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

    public void searchBudgetLines() {
        applyFilters();
    }

    public void filterBudgetLinesByCategory() {
        applyFilters();
    }

    public void filterBudgetLinesByStatus() {
        applyFilters();
    }

    private void applyFilters() {
        if ((searchQuery == null || searchQuery.isEmpty()) && selectedCategory == null  && selectedStatus == null){
            filteredBudgetLines = budgetLineService.getAllBudgetLines();
        } else {
            filteredBudgetLines = budgetLines.stream()
                    .filter(bl -> searchQuery == null || searchQuery.isEmpty() || bl.getTitle().toLowerCase().contains(searchQuery.toLowerCase()))
                    .filter(bl -> selectedCategory == null || bl.getBudgetLineCategory().equals(selectedCategory))
                    .filter(bl -> selectedStatus == null || bl.getStatus().equals(selectedStatus))
                    .collect(Collectors.toList());
        }

    }

    public void searchCategories() {
        if(categorySearchQuery == null || categorySearchQuery.isEmpty()){
            filteredBudgetLineCategories = budgetLineCategoryService.getAllBudgetLineCategories();
            return;
        }
        filteredBudgetLineCategories = budgetLineCategories.stream()
                .filter(blc -> categorySearchQuery.isEmpty() || blc.getCategoryName().toLowerCase().contains(categorySearchQuery.toLowerCase()))
                .collect(Collectors.toList());
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
                Date currentDate = new Date();
                if (currentDate.after(newBudgetLine.getEndDate())){
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current date cannot be after end date", null));
                    return;
                }
                budgetLineService.createBudgetLine(newBudgetLine, budgetLineCategory);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
                filteredBudgetLines.add(newBudgetLine);
                newBudgetLine = new BudgetLine();
                newBudgetLineCategory = new BudgetLineCategory();
                newChartBean.refreshChartData();
                // loadBudgetLines();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Budget Line Category with that name does not exist.", null));
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
            selectedBudgetLine.setBalance(selectedBudgetLine.getInitialAmount());
            selectedBudgetLine.setFloatAmount(selectedBudgetLine.getBalance());
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            // loadBudgetLines();
            newChartBean.refreshChartData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void submitBudgetLine(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.EDIT_BUDGET_LINE)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        try {
            if (!selectedBudgetLine.getStatus().equals(BudgetLineStatus.DRAFT)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot submit a budget line that is not in draft", null));
                return;
            }
            selectedBudgetLine.setBalance(selectedBudgetLine.getInitialAmount());
            selectedBudgetLine.setFloatAmount(selectedBudgetLine.getBalance());
            selectedBudgetLine.setStatus(BudgetLineStatus.SUBMITTED);
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            // loadBudgetLines();
            newChartBean.refreshChartData();
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

    public void requestChanges(){
        if (!selectedBudgetLine.getStatus().equals(BudgetLineStatus.SUBMITTED)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot request changes for a budget line that has not been submitted", null));
            return;
        }
        try {
            if (selectedBudgetLine.getComment() == null || selectedBudgetLine.getComment().equals("")){
                selectedBudgetLine.setComment("Changes Requested");
            }
            selectedBudgetLine.setStatus(BudgetLineStatus.DRAFT);
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Changes requested.", null));
            newChartBean.refreshChartData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
            e.printStackTrace();
        }
    }

    public void approveBudgetLine() {
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.APPROVE_BUDGET_LINE)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedBudgetLine.getStatus().equals(BudgetLineStatus.SUBMITTED)) {
            selectedBudgetLine.setStatus(BudgetLineStatus.APPROVED);
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Budget Line approved", null));
            newChartBean.refreshChartData();
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
        if (selectedBudgetLine.getStatus().equals(BudgetLineStatus.SUBMITTED)) {
            selectedBudgetLine.setStatus(BudgetLineStatus.REJECTED);
            budgetLineService.updateBudgetLine(selectedBudgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Budget Line rejected", null));
            newChartBean.refreshChartData();
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
            List<Requisition> budgetLineRequisitions = requisitionService.getRequisitionsByBudgetLine(budgetLine);
            for (Requisition requisition : budgetLineRequisitions){
                if (requisition.getStatus().equals(RequisitionStatus.DISBURSED) && requisition.getAccountability() == null){
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete a budget line with a requisition which has not yet been provided accountability for.", null));
                    return;
                }
            }

            for (Requisition requisition : budgetLineRequisitions){
                requisitionService.deleteRequisition(requisition);
            }

            budgetLineService.deleteBudgetLine(budgetLine);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            // loadBudgetLines();
            newChartBean.refreshChartData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void loadBudgetLines(){
        try {
            filteredBudgetLines = budgetLineService.getAllBudgetLines();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void loadBudgetLineCategories() {
        try {
            filteredBudgetLineCategories = budgetLineCategoryService.getAllBudgetLineCategories();
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
            filteredBudgetLineCategories.add(newBudgetLineCategory);
            newBudgetLineCategory = new BudgetLineCategory();
            // loadBudgetLineCategories();
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
            // loadBudgetLineCategories();
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
            List<BudgetLine> budgetLineCategoryBudgetLines = budgetLineCategoryService.getBudgetLinesForCategory(category);
            for (BudgetLine budgetLine : budgetLineCategoryBudgetLines){
                if (budgetLine.getStatus().equals(BudgetLineStatus.APPROVED)){
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot delete a budget line category with an active budget line.", null));
                    return;
                }
            }

            for (BudgetLine budgetLine : budgetLineCategoryBudgetLines){
                deleteBudgetLine(budgetLine);
            }
            budgetLineCategoryService.deleteBudgetLineCategory(category);

            loadBudgetLineCategories();
            loadBudgetLines();
            newChartBean.refreshChartData();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public List<BudgetLineCategory> budgetLineCategoryList(String query) {
        Set<BudgetLineCategory> uniqueCategories = new HashSet<>(budgetLineCategoryService.searchBudgetLineCategoriesByName(query));
        return new ArrayList<>(uniqueCategories);
    }

    // Getters and setters
    public BudgetLineCategory getNewBudgetLineCategory() {
        return newBudgetLineCategory;
    }

    public void setNewBudgetLineCategory(BudgetLineCategory newBudgetLineCategory) {
        this.newBudgetLineCategory = newBudgetLineCategory;
    }

    public List<BudgetLineCategory> getBudgetLineCategories() {
        return budgetLineCategoryService.getAllBudgetLineCategories();
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
        List<BudgetLine> allBudgetLines = budgetLineService.getAllBudgetLines();
        Date currentDate = new Date();
        for (BudgetLine budgetLine : allBudgetLines){
            if (currentDate.after(budgetLine.getEndDate())){
                budgetLine.setStatus(BudgetLineStatus.EXPIRED);
            }
        }
        return allBudgetLines;
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

    public List<BudgetLine> getFilteredBudgetLines() {
        Date currentDate = new Date();
        for (BudgetLine budgetLine : filteredBudgetLines){
            if (currentDate.after(budgetLine.getEndDate())){
                budgetLine.setStatus(BudgetLineStatus.EXPIRED);
            }
        }
        newChartBean.refreshChartData();
        return filteredBudgetLines;
    }

    public String convertBudgetLineIdToString(int id){
        return String.format("BL%08d", id);
    }

    public String convertBudgetLineCategoryIdToString(int id){
        return String.format("BC%05d", id);
    }

    public String budgetLinesStringLabel(){
        return filteredBudgetLines.size() != 1 ? " Budget Lines":" Budget Line";
    }

    public void setFilteredBudgetLines(List<BudgetLine> filteredBudgetLines) {
        this.filteredBudgetLines = filteredBudgetLines;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public BudgetLineStatus getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(BudgetLineStatus selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public BudgetLineCategory getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(BudgetLineCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public List<String> getBudgetLineStatuses() {
        return budgetLineStatuses;
    }

    public void setBudgetLineStatuses(List<String> budgetLineStatuses) {
        this.budgetLineStatuses = budgetLineStatuses;
    }

    public List<BudgetLineCategory> getUniqueBudgetLineCategories() {
        Set<BudgetLineCategory> uniqueCategories = new HashSet<>(budgetLineCategoryService.getAllBudgetLineCategories());
        return new ArrayList<>(uniqueCategories);
    }

    public int getActiveBudgetLineCount() {
        return budgetLineService.getActiveBudgetLineCount();
    }

    public void setActiveBudgetLineCount(int activeBudgetLineCount) {
        this.activeBudgetLineCount = activeBudgetLineCount;
    }

    public double getBudgetLineSummedTotal() {
        return budgetLineService.getBudgetLineTotalAmount();
    }

    public void setBudgetLineSummedTotal(double budgetLineSummedTotal) {
        this.budgetLineSummedTotal = budgetLineSummedTotal;
    }

    public String getCategorySearchQuery() {
        return categorySearchQuery;
    }

    public void setCategorySearchQuery(String categorySearchQuery) {
        this.categorySearchQuery = categorySearchQuery;
    }

    public List<BudgetLineCategory> getFilteredBudgetLineCategories() {
        Set<BudgetLineCategory> uniqueCategories = new HashSet<>(filteredBudgetLineCategories);
        return new ArrayList<>(uniqueCategories);
    }

    public String budgetLineCategoryStringLabel(){
        return filteredBudgetLineCategories.size() != 1 ? " Budget Line Categories":" Budget Line Category";
    }

    public void setFilteredBudgetLineCategories(List<BudgetLineCategory> filteredBudgetLineCategories) {
        this.filteredBudgetLineCategories = filteredBudgetLineCategories;
    }

    public void rowSelect(SelectEvent event) {
        currentForm = "review_budget_line";
        selectedBudgetLine = (BudgetLine) event.getObject();
    }

    public String getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm(String currentForm) {
        this.currentForm = currentForm;
    }

    public void cancelCurrentForm(){
        currentForm = null;
    }

    public void prepareCreateBudgetLine(){
        currentForm = "create_budget_line";
    }

    public void prepareEditBudgetLine(BudgetLine budgetLine){
        selectBudgetLine(budgetLine);
        currentForm = "edit_budget_line";
    }
}