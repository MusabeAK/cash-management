package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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

    public void makeRequisition(Requisition requisition, User user) {
        Session session = sessionFactory.getCurrentSession();
        requisition.setUser(user);
        user.getRequisitions().add(requisition);
        session.saveOrUpdate(requisition);
        session.saveOrUpdate(user);
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
        return sessionFactory.getCurrentSession().createQuery("from Requisition").list();
    }

    public List<Requisition> getRequisitionsByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        List<Requisition> requisitions = null;
        String hql = "FROM Requisition rq WHERE rq.user = :user";
        requisitions = session.createQuery(hql, Requisition.class)
                .setParameter("user", user)
                .list();
        return requisitions;
    }

}
