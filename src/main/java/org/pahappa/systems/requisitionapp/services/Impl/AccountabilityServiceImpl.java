package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.AccountabilityDAO;
import org.pahappa.systems.requisitionapp.exceptions.AccountabilityNotFoundException;
import org.pahappa.systems.requisitionapp.exceptions.RequisitionNotFoundException;
import org.pahappa.systems.requisitionapp.models.Accountability;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.services.AccountabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountabilityServiceImpl implements AccountabilityService {

    private final AccountabilityDAO accountabilityDAO;

    @Autowired
    public AccountabilityServiceImpl(AccountabilityDAO accountabilityDAO) {
        this.accountabilityDAO = accountabilityDAO;
    }

    @Override
    public void addAccountabilityToRequisition(Accountability accountability, Requisition requisition) {

        if (accountability == null) {
            throw new AccountabilityNotFoundException("Accountability cannot be null");
        }
        if (requisition == null) {
            throw new RequisitionNotFoundException("Requisition cannot be null");
        }
        accountabilityDAO.addAccountabilityToRequisition(accountability, requisition);
    }

    @Override
    public void updateAccountability(Accountability accountability) {
        if (accountability == null) {
            throw new AccountabilityNotFoundException("Accountability cannot be null");
        }
        accountabilityDAO.updateAccountability(accountability);
    }

    @Override
    public void deleteAccountability(Accountability accountability) {
        if (accountability == null) {
            throw new AccountabilityNotFoundException("Accountability cannot be null");
        }
        accountabilityDAO.deleteAccountability(accountability);
    }

    @Override
    public Accountability getAccountabilityById(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than zero");
        }
        return accountabilityDAO.getAccountabilityById(id);
    }

    @Override
    public List<Accountability> getAllAccountabilities() {
        try {
            return accountabilityDAO.getAllAccountabilities();
        } catch (Exception e) {
            throw new AccountabilityNotFoundException("Error getting all accountabilities");
        }
    }

    @Override
    public Accountability getAccountabilityByRequisition(Requisition requisition) {

        if (requisition == null) {
            throw new RequisitionNotFoundException("Requisition cannot be null");
        }
        return accountabilityDAO.getAccountabilityByRequisition(requisition);
    }

    @Override
    public List<Accountability> searchAccountabilities(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return accountabilityDAO.getAllAccountabilities();
        }
        return accountabilityDAO.searchAccountabilities(searchTerm);
    }

    @Override
    public double getTotalAmountUsed(){
        List<Accountability> accountabilities = accountabilityDAO.getAllAccountabilities();
        return accountabilities.stream().mapToDouble(Accountability::getAmountUsed).sum();
    }
}
