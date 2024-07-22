package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RequisitionService {
    void makeRequisition(Requisition requisition, User user);

    void updateRequisition(Requisition requisition);

    void deleteRequisition(Requisition requisition);

    Requisition getRequisitionById(long id);

    List<Requisition> getAllRequisitions();

    List<Requisition> getRequisitionsByUser(User user) throws UserDoesNotExistException;

    List<Requisition> searchRequisitions(String searchTerm);

    List<BudgetLine> searchBudgetLines(String query);

    double getTotalAmountDisbursed();

    List<Requisition> getRequisitionsByBudgetLine(BudgetLine budgetLine);

    long getTimeBetweenStatuses(Requisition requisition, RequisitionStatus startStatus, RequisitionStatus endStatus);

    long getHRReviewedToCEOApprovedTime(Requisition requisition);

    long getCEOApprovedToDisbursedTime(Requisition requisition);

    long getSubmittedToRejectedTime(Requisition requisition);

    long getSubmittedToHRReviewedTime(Requisition requisition);

    long getSubmittedToCEOApprovedTime(Requisition requisition);

    long getSubmittedToDisbursedTime(Requisition requisition);

    long getTotalProcessingTime(Requisition requisition);

    String formatDuration(long millis);

    long getTimeSinceSubmitted(Requisition requisition);

    String getDayOfWeek(Date date);

    Map<String, Long> getDisbursementFrequencyByDayOfWeek();

    Map<String, Long> getCreationFrequencyByDayOfWeek();
}
