package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.pahappa.systems.requisitionapp.services.UserService;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
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
    private Requisition selectedRequisition;

    @PostConstruct
    public void init() {
        newRequisition = new Requisition();
        requisitions = requisitionService.getAllRequisitions();
        userRequisitions = new ArrayList<>();
    }

    public void makeRequisition() {
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
                              if (requisition.getAccountability() == null) {
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
                      requisitionService.makeRequisition(newRequisition, currentUser);
                      FacesContext.getCurrentInstance().addMessage(null,
                              new FacesMessage(FacesMessage.SEVERITY_INFO, "Success.", null));
                  } else
                      FacesContext.getCurrentInstance().addMessage(null,
                              new FacesMessage(FacesMessage.SEVERITY_ERROR, "No budget line currently", null));
              } else
                  FacesContext.getCurrentInstance().addMessage(null,
                          new FacesMessage(FacesMessage.SEVERITY_ERROR, "No budget line currently", null));
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

    public void deleteRequisition(Requisition requisition){
        try {
            requisitionService.deleteRequisition(requisition);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", null));
            loadUserRequisitions();
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error" + e.getMessage(), null));
        }
    }

    public void selectRequisition(Requisition requisition){
        if (requisition.getStatus().equals(RequisitionStatus.DRAFT)){
            this.selectedRequisition = requisition;
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot edit a requisition that is not in draft.", null));
        }
    }

    public List<BudgetLine> searchBudgetLines(String query){
        return requisitionService.searchBudgetLines(query);
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
        return requisitions;
    }

    public void setRequisitions(List<Requisition> requisitions) {
        this.requisitions = requisitions;
    }

    public List<Requisition> getUserRequisitions() {
        currentUser = getCurrentUser();
        try {
            if (currentUser != null) {
                userRequisitions = requisitionService.getRequisitionsByUser(currentUser);
                return userRequisitions;
            }
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
        return userRequisitions;
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
}
