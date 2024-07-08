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
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            accountability.setRequisition(requisition);
            requisition.setAccountability(accountability);
            session.saveOrUpdate(accountability);
            session.saveOrUpdate(requisition);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void updateAccountability(Accountability accountability) {
        sessionFactory.getCurrentSession().update(accountability);
    }

    public void deleteAccountability(Accountability accountability) {
        sessionFactory.getCurrentSession().delete(accountability);
    }

    public Accountability getAccountabilityById(int id) {
        return (Accountability) sessionFactory.getCurrentSession().get(Accountability.class, id);
    }

    public List<Accountability> getAllAccountabilities() {
        return sessionFactory.getCurrentSession().createQuery("from Accountability").list();
    }

    public Accountability getAccountabilityByRequisition(Requisition requisition) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        Accountability accountability = null;
        try {
            transaction = session.beginTransaction();
            String hql = "FROM Accountability a WHERE a.requisition = :requisition";
            accountability = session.createQuery(hql, Accountability.class)
                    .setParameter("requisition", requisition)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
        return accountability;
    }

}
