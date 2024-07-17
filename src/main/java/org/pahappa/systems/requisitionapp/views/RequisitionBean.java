package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;
import org.pahappa.systems.requisitionapp.models.utils.StatusPipeline;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@ViewScoped
public class RequisitionBean implements Serializable {

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private BudgetLineService budgetLineService;

    private User currentUser;
    private Requisition newRequisition;
    private String budgetLineName;
    private List<Requisition> requisitions;
    private List<Requisition> userRequisitions;
    private List<Requisition> draftRequisitions;
    private List<Requisition> reviewedRequisitions;
    private List<Requisition> approvedRequisitions;
    private Requisition selectedRequisition;
    private String comment;
    private double totalAmountDisbursed;

    @PostConstruct
    public void init() {
        totalAmountDisbursed = requisitionService.getTotalAmountDisbursed();
        newRequisition = new Requisition();
        requisitions = requisitionService.getAllRequisitions();
        userRequisitions = new ArrayList<>();
        approvedRequisitions = new ArrayList<>();
        reviewedRequisitions = new ArrayList<>();
        draftRequisitions = new ArrayList<>();
        comment = "";
        for (Requisition requisition : requisitions) {
            if (requisition.getStatus().equals(RequisitionStatus.HR_REVIEWED)){
                reviewedRequisitions.add(requisition);
            }
        }
        for (Requisition requisition : requisitions) {
            if (requisition.getStatus().equals(RequisitionStatus.SUBMITTED)){
                draftRequisitions.add(requisition);
            }
        }
        for (Requisition requisition : requisitions) {
            if (requisition.getStatus().equals(RequisitionStatus.CEO_APPROVED)){
                approvedRequisitions.add(requisition);
            }
        }
    }

    public void makeRequisition() {
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.CREATE_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        currentUser = getCurrentUser();
        try {
          if (currentUser != null) {
              BudgetLine budgetLine = budgetLineService.getBudgetLineByTitle(budgetLineName);
              if (budgetLine != null ) {
                  if (budgetLine.getStatus().equals(BudgetLineStatus.APPROVED)) {
                      List<Requisition> currentUserRequisitions = requisitionService.getRequisitionsByUser(currentUser);
                      List<Requisition> userRequisitions = currentUser.getRequisitions();
                      if (!currentUserRequisitions.isEmpty() || !userRequisitions.isEmpty()) {
                          for (Requisition requisition : currentUserRequisitions) {
                              if (requisition.getAccountability() == null && !requisition.getStatus().equals(RequisitionStatus.REJECTED)) {
                                  FacesContext.getCurrentInstance().addMessage(null,
                                          new FacesMessage(FacesMessage.SEVERITY_ERROR, "Provide accountability for previous requisition.", null));
                                  return;
                              }
                          }
                      }
                      if (budgetLine.getBalance() < newRequisition.getAmount()){
                          FacesContext.getCurrentInstance().addMessage(null,
                                  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount cannot be greater than budget line balance", null));
                          return;
                      }
                      if (newRequisition.getDateNeeded().after(budgetLine.getEndDate())){
                          FacesContext.getCurrentInstance().addMessage(null,
                                  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be after budget line end date", null));
                          return;
                      }
                      if (newRequisition.getDateNeeded().before(budgetLine.getStartDate())){
                          FacesContext.getCurrentInstance().addMessage(null,
                                  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be before budget line start date", null));
                          return;
                      }
                      if (newRequisition.getAmount() <= 0){
                          FacesContext.getCurrentInstance().addMessage(null,
                                  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount cannot be less than or equal 0", null));
                          return;
                      }
                      newRequisition.setBudgetLine(budgetLine);
                      newRequisition.setUser(currentUser);
                      userRequisitions.add(newRequisition);
                      requisitions.add(newRequisition);
                      requisitionService.makeRequisition(newRequisition, currentUser);
                      FacesContext.getCurrentInstance().addMessage(null,
                              new FacesMessage(FacesMessage.SEVERITY_INFO, "Success.", null));
                      newRequisition = new Requisition();
                  } else
                      FacesContext.getCurrentInstance().addMessage(null,
                              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid budget line", null));
              } else
                  FacesContext.getCurrentInstance().addMessage(null,
                          new FacesMessage(FacesMessage.SEVERITY_ERROR, "No budget line in the system", null));
          } else {
              FacesContext.getCurrentInstance().addMessage(null,
                      new FacesMessage(FacesMessage.SEVERITY_ERROR, "No user logged in", null));
          }
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void updateRequisition(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.EDIT_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedRequisition.getAmount() > selectedRequisition.getBudgetLine().getBalance()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount cannot be greater than budget line balance", null));
            return;
        }
        if (selectedRequisition.getDateNeeded().after(selectedRequisition.getBudgetLine().getEndDate())){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be after budget line end date", null));
            return;
        }
        if (selectedRequisition.getDateNeeded().before(selectedRequisition.getBudgetLine().getStartDate())){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be before budget line start date", null));
            return;
        }
        if (selectedRequisition.getAmount() <= 0){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount cannot be less than or equal 0", null));
            return;
        }
        if (!selectedRequisition.getStatus().equals(RequisitionStatus.DRAFT)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot update a requisition that is not in draft", null));
            return;
        }
        try {
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            loadUserRequisitions();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void submitRequisition(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.CREATE_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedRequisition.getAmount() > selectedRequisition.getBudgetLine().getBalance()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount cannot be greater than budget line balance", null));
            return;
        }
        if (selectedRequisition.getDateNeeded().after(selectedRequisition.getBudgetLine().getEndDate())){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be after budget line end date", null));
            return;
        }
        if (selectedRequisition.getDateNeeded().before(selectedRequisition.getBudgetLine().getStartDate())){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be before budget line start date", null));
            return;
        }
        if (selectedRequisition.getAmount() <= 0){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount cannot be less than or equal 0", null));
            return;
        }
        if (!selectedRequisition.getStatus().equals(RequisitionStatus.DRAFT)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot submit a requisition that is not in draft", null));
            return;
        }
        try {
            selectedRequisition.setStatus(RequisitionStatus.SUBMITTED);
            draftRequisitions.add(newRequisition);
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Submitted", null));
            loadUserRequisitions();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void deleteRequisition(Requisition requisition){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.DELETE_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        try {
            if (requisition.getStatus().equals(RequisitionStatus.DRAFT) || requisition.getStatus().equals(RequisitionStatus.REJECTED)){
                requisitionService.deleteRequisition(requisition);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
                loadUserRequisitions();
            }
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void selectRequisition(Requisition requisition){
        try {
            this.selectedRequisition = requisition;
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: "+ e.getMessage(), null));
        }
    }

    public void reviewAndApproveRequisition(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.REVIEW_REQUISITION) || !LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.APPROVE_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedRequisition.getStatus().equals(RequisitionStatus.SUBMITTED)){
            selectedRequisition.setComment(comment);
            selectedRequisition.setStatus(RequisitionStatus.HR_REVIEWED);
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Requisition reviewed.", null));
            reviewedRequisitions.add(selectedRequisition);
            draftRequisitions.remove(selectedRequisition);
        } else
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot approve a requisition that is not in draft.", null));
    }

    public void reviewAndRejectRequisition(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.REVIEW_REQUISITION) || !LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.REJECT_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedRequisition.getStatus().equals(RequisitionStatus.SUBMITTED)) {
            selectedRequisition.setComment(comment);
            selectedRequisition.setStatus(RequisitionStatus.REJECTED);
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Requisition rejected.", null));
            draftRequisitions.remove(selectedRequisition);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot reject a requisition that is not in draft.", null));
        }
    }

    public void approveRequisition(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.APPROVE_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedRequisition.getStatus().equals(RequisitionStatus.HR_REVIEWED)){
            selectedRequisition.setStatus(RequisitionStatus.CEO_APPROVED);
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Requisition approved.", null));
            approvedRequisitions.add(selectedRequisition);
            reviewedRequisitions.remove(selectedRequisition);
        } else
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot approve a requisition that is not reviewed.", null));
    }

    public void rejectRequisition(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.REJECT_REQUISITION)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedRequisition.getStatus().equals(RequisitionStatus.HR_REVIEWED)){
            selectedRequisition.setStatus(RequisitionStatus.REJECTED);
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Requisition rejected.", null));
            reviewedRequisitions.remove(selectedRequisition);
        } else
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot reject a requisition that is not reviewed.", null));
    }

    public void disburseRequisition(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.DISBURSE_MONEY)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        if (selectedRequisition.getStatus().equals(RequisitionStatus.CEO_APPROVED)){
            if (selectedRequisition.getAmount() > selectedRequisition.getBudgetLine().getBalance()){
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount greater than budget line balance.", null));
                return;
            }
            try {
                selectedRequisition.setStatus(RequisitionStatus.DISBURSED);
                int currentBalance = selectedRequisition.getBudgetLine().getBalance();
                int amountUsed = selectedRequisition.getAmount();
                int newBalance = currentBalance - amountUsed;
                BudgetLine budgetLine = budgetLineService.getBudgetLineById(selectedRequisition.getBudgetLine().getId());
                budgetLine.setBalance(newBalance);
                budgetLineService.updateBudgetLine(budgetLine);
                requisitionService.updateRequisition(selectedRequisition);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Requisition disbursed.", null));
                approvedRequisitions.remove(selectedRequisition);
            } catch (Exception e){
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error disbursing requisition." + e.getMessage(), null));
            }
        }
    }

    public List<BudgetLine> searchBudgetLines(String query){
        List<BudgetLine> searchedBudgetLines = requisitionService.searchBudgetLines(query);
        List<BudgetLine> returnedBudgetLines = new ArrayList<>();
        Date currentDate = new Date();
        for(BudgetLine budgetLine : searchedBudgetLines){
            if (currentDate.after(budgetLine.getEndDate())){
                budgetLine.setStatus(BudgetLineStatus.EXPIRED);
            }
            if (budgetLine.getStatus().equals(BudgetLineStatus.APPROVED)){
                returnedBudgetLines.add(budgetLine);
            }
        }
        return returnedBudgetLines;
    }

    public void loadUserRequisitions(){
        try {
            userRequisitions = getUserRequisitions();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void loadAllRequisitions(){
        try {
            requisitions = requisitionService.getAllRequisitions();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    /*
    to-do
    get different types of requisitions
    get requisitions for current user with accountabilities
     */

    // getters and setters
    public User getCurrentUser() {
        if (currentUser == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                ExternalContext externalContext = context.getExternalContext();
                if (externalContext != null) {
                    currentUser = (User) externalContext.getSessionMap().get("currentUser");
                }
            }
        }
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Requisition getNewRequisition() {
        return newRequisition;
    }

    public void setNewRequisition(Requisition newRequisition) {
        this.newRequisition = newRequisition;
    }

    public String getBudgetLineName() {
        return budgetLineName;
    }

    public void setBudgetLineName(String budgetLineName) {
        this.budgetLineName = budgetLineName;
    }

    public List<Requisition> getRequisitions() {
        List<Requisition> allRequisitions = requisitionService.getAllRequisitions();
        List<Requisition> returnedRequisitions = new ArrayList<>();
        for(Requisition requisition : allRequisitions){
            if (!requisition.getStatus().equals(RequisitionStatus.DRAFT)){
                returnedRequisitions.add(requisition);
            }
        }
        returnedRequisitions.sort((r1, r2) -> Integer.compare(r2.getId(), r1.getId()));
        return returnedRequisitions;
    }

    public void setRequisitions(List<Requisition> requisitions) {
        this.requisitions = requisitions;
    }

    public List<Requisition> getUserRequisitions() {
        currentUser = LoginBean.getCurrentUser();
        if (currentUser != null) {
            try {
                userRequisitions = requisitionService.getRequisitionsByUser(currentUser);
                userRequisitions.sort((r1, r2) -> Integer.compare(r2.getId(), r1.getId()));
                return userRequisitions;
            } catch (Exception e){
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
                return Collections.emptyList();
            }
        } else
            return Collections.emptyList();
    }

    public void setUserRequisitions(List<Requisition> userRequisitions) {
        this.userRequisitions = userRequisitions;
    }

    public Requisition getSelectedRequisition() {
        return selectedRequisition;
    }

    public void setSelectedRequisition(Requisition selectedRequisition) {
        this.selectedRequisition = selectedRequisition;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Requisition> getReviewedRequisitions() {
        return reviewedRequisitions;
    }

    public void setReviewedRequisitions(List<Requisition> reviewedRequisitions) {
        this.reviewedRequisitions = reviewedRequisitions;
    }

    public List<Requisition> getDraftRequisitions() {
        return draftRequisitions;
    }

    public void setDraftRequisitions(List<Requisition> draftRequisitions) {
        this.draftRequisitions = draftRequisitions;
    }

    public List<Requisition> getApprovedRequisitions() {
        return approvedRequisitions;
    }

    public void setApprovedRequisitions(List<Requisition> approvedRequisitions) {
        this.approvedRequisitions = approvedRequisitions;
    }

    public double getTotalAmountDisbursed() {
        return requisitionService.getTotalAmountDisbursed();
    }

    public void setTotalAmountDisbursed(double totalAmountDisbursed) {
        this.totalAmountDisbursed = totalAmountDisbursed;
    }

    public void rowSelect(SelectEvent event) {
        selectedRequisition = (Requisition) event.getObject();
    }

    public int getOverallProgress() {
        if (selectedRequisition != null) {
            return StatusPipeline.getOverallProgress(selectedRequisition.getStatus());
        }
        return 0;
    }
}
