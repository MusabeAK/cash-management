package org.pahappa.systems.requisitionapp.views;

import com.sun.faces.application.NavigationHandlerImpl;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.*;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.pahappa.systems.requisitionapp.views.utils.NewChartBean;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ViewScoped
public class RequisitionBean implements Serializable {

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private BudgetLineService budgetLineService;

    @Autowired
    private NewChartBean newChartBean;

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
    private String searchQuery;
    private List<Requisition> filteredRequisitions;
    private double totalAmountDisbursed;

    private long totalProcessingTime;
    private long ReviewedToApprovedTime;
    private long ApprovedToDisbursedTime;
    private long SubmittedToRejectedTime;
    private long SubmittedToReviewedTime;
    private long SubmittedToApprovedTime;
    private long SubmittedToDisbursedTime;
    private long TimeSinceSubmitted;

    private MenuModel stepModel;
    private int currentStep;

    @PostConstruct
    public void init() {
        createStepModel();
        totalProcessingTime = 0L;
        ReviewedToApprovedTime = 0L;
        ApprovedToDisbursedTime = 0L;
        SubmittedToRejectedTime = 0L;
        SubmittedToReviewedTime = 0L;
        SubmittedToApprovedTime = 0L;
        SubmittedToDisbursedTime = 0L;
        TimeSinceSubmitted = 0L;


        totalAmountDisbursed = requisitionService.getTotalAmountDisbursed();
        newRequisition = new Requisition();
        requisitions = requisitionService.getAllRequisitions();
        filteredRequisitions = requisitionService.getAllRequisitions();
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

    public MenuModel getStepModel() {
        return stepModel;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    private void createStepModel() {
        stepModel = new DefaultMenuModel();

        // Create steps using the builder pattern
        DefaultMenuItem step1 = DefaultMenuItem.builder()
                .value("Submitted")
                .build();

        DefaultMenuItem step2 = DefaultMenuItem.builder()
                .value("Reviewed")
                .build();

        DefaultMenuItem step3 = DefaultMenuItem.builder()
                .value("Approved")
                .build();

        DefaultMenuItem step4 = DefaultMenuItem.builder()
                .value("Disbursed")
                .build();

        // Add steps to the model
        stepModel.getElements().add(step1);
        stepModel.getElements().add(step2);
        stepModel.getElements().add(step3);
        stepModel.getElements().add(step4);


        // Set current step based on your logic
        currentStep = 2; // For example, the current step is "Reviewed"
    }

    public void makeRequisition() {
        Date currentDate = new Date();

        // Remove time part from current date
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(currentDate);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date currentDateOnly = cal1.getTime();

        // Remove time part from date needed
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(newRequisition.getDateNeeded());
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        Date dateNeededOnly = cal2.getTime();

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
                              if (!requisition.getAccountability().getStatus().equals(AccountabilityStatus.APPROVED)){
                                  FacesContext.getCurrentInstance().addMessage(null,
                                          new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot make another requisition until previous requisition's accountability is approved.", null));
                                  return;
                              }
                          }
                      }
                      if (budgetLine.getBalance() < newRequisition.getAmount()){
                          FacesContext.getCurrentInstance().addMessage(null,
                                  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Amount cannot be greater than budget line balance", null));
                          return;
                      }
                      if (budgetLine.getFloatAmount() < newRequisition.getAmount()){
                          FacesContext.getCurrentInstance().addMessage(null,
                                  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to make requisition at the moment", null));
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
                      if (dateNeededOnly.before(currentDateOnly)){
                          FacesContext.getCurrentInstance().addMessage(null,
                                  new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be before current date", null));
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
                      loadUserRequisitions();
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
            e.printStackTrace();
        }
    }

    public void updateRequisition(){
        Date currentDate = new Date();

        // Remove time part from current date
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(currentDate);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date currentDateOnly = cal1.getTime();

        // Remove time part from date needed
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(selectedRequisition.getDateNeeded());
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        Date dateNeededOnly = cal2.getTime();

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
        if (dateNeededOnly.before(currentDateOnly)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be before current date", null));
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
        Date currentDate = new Date();

        // Remove time part from current date
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(currentDate);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        Date currentDateOnly = cal1.getTime();

        // Remove time part from date needed
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(selectedRequisition.getDateNeeded());
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        Date dateNeededOnly = cal2.getTime();

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
        if (selectedRequisition.getBudgetLine().getFloatAmount() < selectedRequisition.getAmount()){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to make requisition at the moment", null));
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
        if (dateNeededOnly.before(currentDateOnly)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Date Needed cannot be before current date", null));
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
            BudgetLine budgetLine = budgetLineService.getBudgetLineById(selectedRequisition.getBudgetLine().getId());
            int amountUsed = selectedRequisition.getAmount();
            int currentFloatAmount = budgetLine.getFloatAmount();
            int newFloatAmount = currentFloatAmount - amountUsed;

            budgetLine.setFloatAmount(newFloatAmount);
            budgetLineService.updateBudgetLine(budgetLine);

            draftRequisitions.add(newRequisition);
            selectedRequisition.setComment("");
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Submitted", null));
            loadUserRequisitions();
            newChartBean.refreshChartData();
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
                newChartBean.refreshChartData();
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
//            selectedRequisition.setComment(comment);
//            comment = "";
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
//            selectedRequisition.setComment(comment);
//            comment = "";
            BudgetLine budgetLine = budgetLineService.getBudgetLineById(selectedRequisition.getBudgetLine().getId());
            budgetLine.setFloatAmount(budgetLine.getBalance());
            budgetLineService.updateBudgetLine(budgetLine);
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
//            selectedRequisition.setComment(comment);
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
            BudgetLine budgetLine = budgetLineService.getBudgetLineById(selectedRequisition.getBudgetLine().getId());
            budgetLine.setFloatAmount(budgetLine.getBalance());
            budgetLineService.updateBudgetLine(budgetLine);
            selectedRequisition.setStatus(RequisitionStatus.REJECTED);
            requisitionService.updateRequisition(selectedRequisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Requisition rejected.", null));
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
                budgetLine.setFloatAmount(budgetLine.getBalance());
                budgetLineService.updateBudgetLine(budgetLine);
                requisitionService.updateRequisition(selectedRequisition);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Requisition disbursed.", null));
                approvedRequisitions.remove(selectedRequisition);
                newChartBean.refreshChartData();
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

    public void searchUserRequisitions(){
        if (!(searchQuery == null || searchQuery.isEmpty())) {
            filteredRequisitions = requisitionService.searchRequisitions(searchQuery);
            return;
        }
        filteredRequisitions = requisitionService.getAllRequisitions();
    }

    public void searchAllRequisitions(){
        if(searchQuery == null || searchQuery.isEmpty()){
            return;
        }
        requisitions = requisitions.stream()
                .filter(req -> searchQuery.isEmpty()
                        || req.getSubject().toLowerCase().contains(searchQuery.toLowerCase())
                        || req.getComment().toLowerCase().contains(searchQuery.toLowerCase())
                        || req.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void requestChanges(){
        if (!(LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.REJECT_REQUISITION) || LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.APPROVE_REQUISITION))){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        try {
            if (selectedRequisition.getStatus().equals(RequisitionStatus.HR_REVIEWED) || selectedRequisition.getStatus().equals(RequisitionStatus.SUBMITTED)){
                BudgetLine budgetLine = budgetLineService.getBudgetLineById(selectedRequisition.getBudgetLine().getId());
                budgetLine.setFloatAmount(budgetLine.getBalance());
                budgetLineService.updateBudgetLine(budgetLine);
                selectedRequisition.setStatus(RequisitionStatus.DRAFT);
//                selectedRequisition.setComment(comment);
                if (selectedRequisition.getComment().isEmpty() || selectedRequisition.getComment() == null){
                    selectedRequisition.setComment("Changes Requested");
                }
                requisitionService.updateRequisition(selectedRequisition);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Changes requested.", null));
                requisitions.remove(selectedRequisition);
                comment = "";
                newChartBean.refreshChartData();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error.", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void loadUserRequisitions(){
        try {
            userRequisitions = getUserRequisitions();
            filteredRequisitions = getUserRequisitions();
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

    public String convertIdToString(int id){
        return String.format("RQ%08d", id);
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

    public String requisitionsStringLabel(){
        return filteredRequisitions.size() != 1 ? " Requisitions":" Requisition";
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

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<Requisition> getFilteredRequisitions() {
        return filteredRequisitions;
    }

    public void setFilteredRequisitions(List<Requisition> filteredRequisitions) {
        this.filteredRequisitions = filteredRequisitions;
    }

    public double getTotalAmountDisbursed() {
        return requisitionService.getTotalAmountDisbursed();
    }

    public void setTotalAmountDisbursed(double totalAmountDisbursed) {
        this.totalAmountDisbursed = totalAmountDisbursed;
    }

    public String getReviewedToApprovedTime() {
        long time =  requisitionService.getHRReviewedToCEOApprovedTime(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setReviewedToApprovedTime(long reviewedToApprovedTime) {
        ReviewedToApprovedTime = reviewedToApprovedTime;
    }

    public String getTotalProcessingTime() {
        long time = requisitionService.getTotalProcessingTime(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setTotalProcessingTime(long totalProcessingTime) {
        this.totalProcessingTime = totalProcessingTime;
    }

    public String getApprovedToDisbursedTime() {
        long time = requisitionService.getCEOApprovedToDisbursedTime(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setApprovedToDisbursedTime(long approvedToDisbursedTime) {
        ApprovedToDisbursedTime = approvedToDisbursedTime;
    }

    public String getSubmittedToRejectedTime() {
        long time = requisitionService.getSubmittedToRejectedTime(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setSubmittedToRejectedTime(long submittedToRejectedTime) {
        SubmittedToRejectedTime = submittedToRejectedTime;
    }

    public String getSubmittedToReviewedTime() {
        long time = requisitionService.getSubmittedToHRReviewedTime(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setSubmittedToReviewedTime(long submittedToReviewedTime) {
        SubmittedToReviewedTime = submittedToReviewedTime;
    }

    public String getSubmittedToApprovedTime() {
        long time = requisitionService.getSubmittedToCEOApprovedTime(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setSubmittedToApprovedTime(long submittedToApprovedTime) {
        SubmittedToApprovedTime = submittedToApprovedTime;
    }

    public String getSubmittedToDisbursedTime() {
        long time = requisitionService.getSubmittedToDisbursedTime(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setSubmittedToDisbursedTime(long submittedToDisbursedTime) {
        SubmittedToDisbursedTime = submittedToDisbursedTime;
    }

    public String getTimeSinceSubmitted() {
        long time = requisitionService.getTimeSinceSubmitted(selectedRequisition);
        return requisitionService.formatDuration(time);
    }

    public void setTimeSinceSubmitted(long timeSinceSubmitted) {
        TimeSinceSubmitted = timeSinceSubmitted;
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

    // Method to check if a step is active
    public boolean isStepActive(int stepIndex) {
        return stepIndex <= getOverallProgress();
    }
}
