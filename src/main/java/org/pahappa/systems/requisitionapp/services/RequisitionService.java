package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;

import java.util.List;

public interface RequisitionService {
    void makeRequisition(Requisition requisition, User user);

    void updateRequisition(Requisition requisition);

    void deleteRequisition(Requisition requisition);

    Requisition getRequisitionById(long id);

    List<Requisition> getAllRequisitions();

    List<Requisition> getRequisitionsByUser(User user);

    List<Requisition> searchRequisitions(String searchTerm);

}
