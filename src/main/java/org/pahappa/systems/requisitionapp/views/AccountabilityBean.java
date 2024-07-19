package org.pahappa.systems.requisitionapp.views;

import org.pahappa.systems.requisitionapp.models.Accountability;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.AccountabilityStatus;
import org.pahappa.systems.requisitionapp.models.utils.Permission;
import org.pahappa.systems.requisitionapp.services.AccountabilityService;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
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
    private Accountability selectedAccountability;
    private double totalAmountUsed;

    @PostConstruct
    public void init() {
        newAccountability = new Accountability();
        allAccountabilities = accountabilityService.getAllAccountabilities();
        selectedAccountability = new Accountability();
        totalAmountUsed = accountabilityService.getTotalAmountUsed();
    }

    public void addAccountability() {
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.CREATE_ACCOUNTABILITY)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        handleFileUpload();
        if (selectedRequisition.getAccountability() != null){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Already added accountability", null));
            return;
        }
        try {
            int amountRequested = selectedRequisition.getAmount();
            int amountUsed = newAccountability.getAmountUsed();
            if (amountUsed > amountRequested) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot use more than was requested", null));
                return;
            }

            newAccountability.setStatus(AccountabilityStatus.SUBMITTED);
            accountabilityService.addAccountabilityToRequisition(newAccountability, selectedRequisition);
            newAccountability = new Accountability();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Accountability added", null));
            uploadedFile = null;
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void updateAccountability(){
        if (!LoginBean.getCurrentUser().getRole().getPermissions().contains(Permission.CREATE_ACCOUNTABILITY)){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Current user does not have permission to access this function.", null));
            return;
        }
        handleFileUpload();
        try {
            int amountRequested = selectedAccountability.getRequisition().getAmount();
            int amountUsed = selectedAccountability.getAmountUsed();
            if (amountUsed > amountRequested) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot use more than was requested", null));
                return;
            }
            selectedAccountability.setStatus(AccountabilityStatus.SUBMITTED);
            accountabilityService.updateAccountability(selectedAccountability);
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void acceptAccountability(){
        try {
            if (selectedAccountability != null){
                BudgetLine budgetLine = budgetLineService.getBudgetLineById(selectedRequisition.getBudgetLine().getId());
                int amountRequested = selectedRequisition.getAmount();
                int amountUsed = selectedAccountability.getAmountUsed();
                if (amountUsed > amountRequested) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot use more than was requested", null));
                    return;
                }
                int disparity = amountRequested - amountUsed;
                int newBudgetLineBalance = budgetLine.getBalance() + disparity;
                budgetLine.setBalance(newBudgetLineBalance);
                budgetLine.setFloatAmount(budgetLine.getBalance());

                budgetLineService.updateBudgetLine(budgetLine);
                selectedAccountability.setStatus(AccountabilityStatus.APPROVED);
                accountabilityService.updateAccountability(selectedAccountability);
            }
        } catch (Exception e){
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void rejectAccountability(){
        try {
            if (selectedAccountability != null){
                selectedAccountability.setStatus(AccountabilityStatus.REJECTED);
                accountabilityService.updateAccountability(selectedAccountability);
            }
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
            this.selectedAccountability = requisition.getAccountability();
            if (this.selectedAccountability == null) {
                this.selectedAccountability = new Accountability();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + e.getMessage(), null));
        }
    }

    public void selectAccountability(Accountability accountability){
        this.selectedAccountability = accountability;
    }

    // Method to download image
    public StreamedContent getImageDownload() {
        if (selectedAccountability != null && selectedAccountability.getImage() != null) {
            byte[] imageData = selectedAccountability.getImage();
            return DefaultStreamedContent.builder()
                    .name(selectedRequisition.getUser().getUsername() + ": " + selectedRequisition.getSubject() + "accountability.png")
                    .contentType("image/jpeg")
                    .stream(() -> new ByteArrayInputStream(imageData))
                    .build();
        }
        return null;
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

    public Accountability getSelectedAccountability() {
        return selectedAccountability;
    }

    public void setSelectedAccountability(Accountability selectedAccountability) {
        this.selectedAccountability = selectedAccountability;
    }

    public String getBase64Image(byte[] image) {
        return DatatypeConverter.printBase64Binary(image);
    }

    public double getTotalAmountUsed() {
        return accountabilityService.getTotalAmountUsed();
    }

    public void setTotalAmountUsed(double totalAmountUsed) {
        this.totalAmountUsed = totalAmountUsed;
    }
}
