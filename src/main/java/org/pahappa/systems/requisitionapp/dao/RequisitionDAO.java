package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pahappa.systems.requisitionapp.models.Accountability;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RequisitionDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public RequisitionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void makeRequisition(Requisition requisition) {
        sessionFactory.getCurrentSession().save(requisition);
    }

    public void updateRequisition(Requisition requisition) {
        sessionFactory.getCurrentSession().update(requisition);
    }

    public void deleteRequisition(Requisition requisition) {
        sessionFactory.getCurrentSession().delete(requisition);
    }

    public Requisition getRequisitionById(long id) {
        return (Requisition) sessionFactory.getCurrentSession().get(Requisition.class, id);
    }

    public List<Requisition> getAllRequisitions() {
//        return sessionFactory.getCurrentSession().createQuery("from Requisition").list();
        return sessionFactory.getCurrentSession().createQuery("from Requisition", Requisition.class).list();

    }

    public List<Requisition> getRequisitionsByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        List<Requisition> requisitions;
        String hql = "FROM Requisition rq WHERE rq.user = :user";
        requisitions = session.createQuery(hql, Requisition.class)
                .setParameter("user", user)
                .list();
        return requisitions;
    }

    public List<Requisition> getRequisitionsByBudgetLine(BudgetLine budgetLine) {
        Session session = sessionFactory.getCurrentSession();
        List<Requisition> requisitions;
        String hql = "FROM Requisition rq WHERE rq.budgetLine = :budgetLine";
        requisitions = session.createQuery(hql, Requisition.class)
                .setParameter("budgetLine", budgetLine)
                .list();
        return requisitions;
    }

    public List<Requisition> searchRequisitions(String searchTerm) {
        String query = "FROM Requisition WHERE description LIKE :searchTerm " +
                "OR subject LIKE :searchTerm";
        return sessionFactory.getCurrentSession()
                .createQuery(query, Requisition.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .getResultList();
    }

    public List<Requisition> searchRequisitionsByUser(String searchTerm, User user) {
        String query = "FROM Requisition WHERE user.username LIKE :user and (description LIKE :searchTerm " +
                "OR subject LIKE :searchTerm)";
        return sessionFactory.getCurrentSession()
                .createQuery(query, Requisition.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .setParameter("user",   "%"+user.getUsername() + "%" )
                .getResultList();
    }

}
