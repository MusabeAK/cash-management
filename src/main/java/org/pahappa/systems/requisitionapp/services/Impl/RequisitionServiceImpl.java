package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.RequisitionDAO;
import org.pahappa.systems.requisitionapp.exceptions.UserDoesNotExistException;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.pahappa.systems.requisitionapp.models.utils.RequisitionStatus;
import org.pahappa.systems.requisitionapp.services.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RequisitionServiceImpl implements RequisitionService {

    private final RequisitionDAO requisitionDAO;

    @Autowired
    public RequisitionServiceImpl(RequisitionDAO requisitionDAO) {
        this.requisitionDAO = requisitionDAO;
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
}
