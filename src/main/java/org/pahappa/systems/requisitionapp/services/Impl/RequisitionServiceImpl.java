package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.BudgetLineDAO;
import org.pahappa.systems.requisitionapp.dao.RequisitionDAO;
import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.*;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class RequisitionServiceImpl implements RequisitionService {

    private final RequisitionDAO requisitionDAO;
    private final BudgetLineDAO budgetLineDAO;

    @Autowired
    public RequisitionServiceImpl(RequisitionDAO requisitionDAO, BudgetLineDAO budgetLineDAO) {
        this.requisitionDAO = requisitionDAO;
        this.budgetLineDAO = budgetLineDAO;
    }

    @Override
    public void makeRequisition(Requisition requisition, User user) {
        if (requisition == null) {
            throw new IllegalArgumentException("Requisition cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        requisition.setStatus(RequisitionStatus.DRAFT);
        requisitionDAO.makeRequisition(requisition);
    }

    @Override
    public void updateRequisition(Requisition requisition) {
        if (requisition == null || requisition.getId() == 0) {
            throw new IllegalArgumentException("Invalid requisition provided for update");
        }
        requisitionDAO.updateRequisition(requisition);
    }

    @Override
    public void deleteRequisition(Requisition requisition) {
        if (requisition == null || requisition.getId() == 0) {
            throw new IllegalArgumentException("Invalid requisition provided for deletion");
        }
        requisitionDAO.deleteRequisition(requisition);
    }

    @Override
    public Requisition getRequisitionById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }
        return requisitionDAO.getRequisitionById(id);
    }

    @Override
    public List<Requisition> getAllRequisitions() {
        return requisitionDAO.getAllRequisitions();
    }

    @Override
    public List<Requisition> getRequisitionsByUser(User user) throws UserDoesNotExistException {
        if (user == null || user.getId() == 0) {
            throw new UserDoesNotExistException("Invalid user provided for fetching requisitions");
        }
        return requisitionDAO.getRequisitionsByUser(user);
    }

    @Override
    public List<Requisition> searchRequisitions(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return requisitionDAO.getAllRequisitions();
        }
        return requisitionDAO.searchRequisitions(searchTerm);
    }

    @Override
    public List<BudgetLine> searchBudgetLines(String query){
        List<BudgetLine> budgetLines = budgetLineDAO.getAllBudgetLines();
        if (query == null || query.trim().isEmpty()) {
            return budgetLines;
        }
        return budgetLines.stream()
                .filter(budgetLine -> budgetLine.getTitle().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalAmountDisbursed(){
        List<Requisition> requisitions = requisitionDAO.getAllRequisitions();
        List<Requisition> disbursedRequisitions = new ArrayList<>();
        for (Requisition requisition : requisitions) {
            if (requisition.getStatus() == RequisitionStatus.DISBURSED) {
                disbursedRequisitions.add(requisition);
            }
        }
        return disbursedRequisitions.stream().mapToDouble(Requisition::getAmount).sum();
    }

    public List<Requisition> getRequisitionsByBudgetLine(BudgetLine budgetLine){
        if(budgetLine == null)
           return Collections.emptyList();

        return requisitionDAO.getRequisitionsByBudgetLine(budgetLine);
    }

    @Override
    public long getTimeBetweenStatuses(Requisition requisition, RequisitionStatus startStatus, RequisitionStatus endStatus) {
        Date startDate = getStatusTimestamp(requisition, startStatus);
        Date endDate = getStatusTimestamp(requisition, endStatus);

        if (startDate == null || endDate == null) {
            return -1; // Indicate that one or both timestamps are missing
        }

        return endDate.getTime() - startDate.getTime();
    }


    @Override
    public long getTimeSinceSubmitted(Requisition requisition){
        Date startDate = getStatusTimestamp(requisition, RequisitionStatus.SUBMITTED);
        Date endDate;

        if (requisition.getStatus().equals(RequisitionStatus.DISBURSED)){
            endDate = getStatusTimestamp(requisition, RequisitionStatus.DISBURSED);
            if (startDate == null || endDate == null) {
                return -1; // Indicate that one or both timestamps are missing
            }
        } else {
            endDate = new Date();
            if (startDate == null) {
                return -1; // Indicate that one or both timestamps are missing
            }
        }
        return endDate.getTime() - startDate.getTime();

    }

    @Override
    public long getSubmittedToCEOApprovedTime(Requisition requisition) {
        return getTimeBetweenStatuses(requisition, RequisitionStatus.SUBMITTED, RequisitionStatus.CEO_APPROVED);
    }

    @Override
    public long getHRReviewedToCEOApprovedTime(Requisition requisition) {
        return getTimeBetweenStatuses(requisition, RequisitionStatus.HR_REVIEWED, RequisitionStatus.CEO_APPROVED);
    }

    @Override
    public long getCEOApprovedToDisbursedTime(Requisition requisition) {
        return getTimeBetweenStatuses(requisition, RequisitionStatus.CEO_APPROVED, RequisitionStatus.DISBURSED);
    }

    @Override
    public long getSubmittedToRejectedTime(Requisition requisition) {
        return getTimeBetweenStatuses(requisition, RequisitionStatus.SUBMITTED, RequisitionStatus.REJECTED);
    }

    @Override
    public long getSubmittedToDisbursedTime(Requisition requisition) {
        return getTimeBetweenStatuses(requisition, RequisitionStatus.SUBMITTED, RequisitionStatus.DISBURSED);
    }

    @Override
    public long getSubmittedToHRReviewedTime(Requisition requisition) {
        return getTimeBetweenStatuses(requisition, RequisitionStatus.SUBMITTED, RequisitionStatus.HR_REVIEWED);
    }

    @Override
    public long getTotalProcessingTime(Requisition requisition) {
        Date submittedTimestamp = getStatusTimestamp(requisition, RequisitionStatus.SUBMITTED);
        if (submittedTimestamp == null) {
            return -1; // Indicate that the draft timestamp is missing
        }

        Date endDate = getLatestTimestamp(requisition);
        if (endDate == null) {
            return -1; // Indicate that no status has been set after DRAFT
        }

        return endDate.getTime() - submittedTimestamp.getTime();
    }

    @Override
    public String formatDuration(long millis) {
        if (millis < 0) {
            return "N/A";
        }
        return String.format("%d days, %d hours, %d minutes",
                TimeUnit.MILLISECONDS.toDays(millis),
                TimeUnit.MILLISECONDS.toHours(millis) % 24,
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60);
    }

    private Date getStatusTimestamp(Requisition requisition, RequisitionStatus status) {
        switch (status) {
            case DRAFT:
                return requisition.getDraftTimestamp();
            case HR_REVIEWED:
                return requisition.getHrReviewedTimestamp();
            case CEO_APPROVED:
                return requisition.getCeoApprovedTimestamp();
            case REJECTED:
                return requisition.getRejectedTimestamp();
            case DISBURSED:
                return requisition.getDisbursedTimestamp();
            case SUBMITTED:
                return requisition.getSubmittedTimestamp();
            default:
                return null;
        }
    }

    private Date getLatestTimestamp(Requisition requisition) {
        Date latest = requisition.getDraftTimestamp();
        if (requisition.getSubmittedTimestamp() != null && requisition.getSubmittedTimestamp().after(latest)) latest = requisition.getSubmittedTimestamp();
        if (requisition.getHrReviewedTimestamp() != null && requisition.getHrReviewedTimestamp().after(latest)) latest = requisition.getHrReviewedTimestamp();
        if (requisition.getCeoApprovedTimestamp() != null && requisition.getCeoApprovedTimestamp().after(latest)) latest = requisition.getCeoApprovedTimestamp();
        if (requisition.getRejectedTimestamp() != null && requisition.getRejectedTimestamp().after(latest)) latest = requisition.getRejectedTimestamp();
        if (requisition.getDisbursedTimestamp() != null && requisition.getDisbursedTimestamp().after(latest)) latest = requisition.getDisbursedTimestamp();
        return latest;
    }
}
