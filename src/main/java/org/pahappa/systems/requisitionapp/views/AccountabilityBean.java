package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.Accountability;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.services.AccountabilityService;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

@Component
@ViewScoped
public class AccountabilityBean implements Serializable {

    @Autowired
    private AccountabilityService accountabilityService;

    @Autowired
    private RequisitionService requisitionService;

    @Autowired
    private BudgetLineService budgetLineService;

    private User currentUser;
    private Accountability newAccountability;
    private Requisition selectedRequisition;
    private UploadedFile uploadedFile;
    private List<Accountability> allAccountabilities;

    @PostConstruct
    public void init() {
        newAccountability = new Accountability();
        allAccountabilities = accountabilityService.getAllAccountabilities();
    }

    public void addAccountability() {
        handleFileUpload();
        if (selectedRequisition.getAccountability() != null){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Already added accountability", null));
            return;
        }
        try {
            BudgetLine budgetLine = budgetLineService.getBudgetLineById(selectedRequisition.getBudgetLine().getId());
            int amountRequested = selectedRequisition.getAmount();
            int amountUsed = newAccountability.getAmountUsed();
            if (amountUsed > amountRequested) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot use more than was requested", null));
                return;
            }
            int disparity = amountRequested - amountUsed;
            int newBudgetLineBalance = budgetLine.getBalance() + disparity;
            accountabilityService.addAccountabilityToRequisition(newAccountability, selectedRequisition);
            budgetLine.setBalance(newBudgetLineBalance);
            budgetLineService.updateBudgetLine(budgetLine);
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    // Method to handle file upload
    public void handleFileUpload() {
        if (uploadedFile != null) {
            try {
                InputStream inputStream = uploadedFile.getInputStream();
                byte[] fileContent = new byte[(int) uploadedFile.getSize()];
                inputStream.read(fileContent);
                newAccountability.setImage(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void selectRequisition(Requisition requisition) {
        try {
            this.selectedRequisition = requisition;
        }catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: "+ e.getMessage(), null));
        }
    }

    /*
    to-do
    get all accountabilities (done)
     */

    // getters and setters
    public Accountability getNewAccountability() {
        return newAccountability;
    }

    public void setNewAccountability(Accountability newAccountability) {
        this.newAccountability = newAccountability;
    }

    public Requisition getSelectedRequisition() {
        return selectedRequisition;
    }

    public void setSelectedRequisition(Requisition selectedRequisition) {
        this.selectedRequisition = selectedRequisition;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public List<Accountability> getAllAccountabilities() {
        return allAccountabilities;
    }

    public void setAllAccountabilities(List<Accountability> allAccountabilities) {
        this.allAccountabilities = allAccountabilities;
    }
}
