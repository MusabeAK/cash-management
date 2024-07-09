package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pahappa.systems.requisitionapp.models.Accountability;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountabilityDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public AccountabilityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addAccountabilityToRequisition(Accountability accountability, Requisition requisition) {
        Session session = sessionFactory.getCurrentSession();
        accountability.setRequisition(requisition);
        requisition.setAccountability(accountability);
        session.saveOrUpdate(accountability);
        session.saveOrUpdate(requisition);
    }

    public void updateAccountability(Accountability accountability) {
        sessionFactory.getCurrentSession().update(accountability);
    }

    public void deleteAccountability(Accountability accountability) {
        sessionFactory.getCurrentSession().delete(accountability);
    }

    public Accountability getAccountabilityById(long id) {
        return sessionFactory.getCurrentSession().get(Accountability.class, id);
    }

    public List<Accountability> getAllAccountabilities() {
        return sessionFactory.getCurrentSession().createQuery("from Accountability", Accountability.class).list();
    }

    public Accountability getAccountabilityByRequisition(Requisition requisition) {
        String hql = "FROM Accountability a WHERE a.requisition = :requisition";
        return sessionFactory.getCurrentSession().createQuery(hql, Accountability.class)
                .setParameter("requisition", requisition)
                .uniqueResult();
    }

    public List<Accountability> searchAccountabilities(String searchTerm) {
        String query = "FROM Accountability WHERE description LIKE :searchTerm ";
        return sessionFactory.getCurrentSession()
                .createQuery(query, Accountability.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .getResultList();
    }
}
