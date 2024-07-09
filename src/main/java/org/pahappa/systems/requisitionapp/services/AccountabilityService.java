package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.Accountability;
import org.pahappa.systems.requisitionapp.models.Requisition;

import java.util.List;

public interface AccountabilityService {
    void addAccountabilityToRequisition(Accountability accountability, Requisition requisition);

    void updateAccountability(Accountability accountability);

    void deleteAccountability(Accountability accountability);

    Accountability getAccountabilityById(long id);

    List<Accountability> getAllAccountabilities();

    Accountability getAccountabilityByRequisition(Requisition requisition);

    List<Accountability> searchAccountabilities(String searchTerm);
}
