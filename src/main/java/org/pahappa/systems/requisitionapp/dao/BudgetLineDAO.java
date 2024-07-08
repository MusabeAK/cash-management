package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BudgetLineDAO {

    private final SessionFactory sessionFactory;

    public BudgetLineDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addBudgetLineToBudgetLineCategory(BudgetLine budgetLine, BudgetLineCategory budgetLineCategory) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            budgetLine.setBudgetLineCategory(budgetLineCategory);
            budgetLineCategory.getBudgetLines().add(budgetLine);
            session.saveOrUpdate(budgetLine);
            session.saveOrUpdate(budgetLineCategory);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void updateBudgetLine(BudgetLine budgetLine) {
        sessionFactory.getCurrentSession().update(budgetLine);
    }

    public void deleteBudgetLine(BudgetLine budgetLine) {
        sessionFactory.getCurrentSession().delete(budgetLine);
    }

    public BudgetLine getBudgetLineById(long id) {
        return (BudgetLine) sessionFactory.getCurrentSession().get(BudgetLine.class, id);
    }

    public List<BudgetLine> getAllBudgetLines() {
        return sessionFactory.getCurrentSession().createCriteria(BudgetLine.class).list();
    }

    public List<BudgetLine> getAllBudgetLinesForCategory(BudgetLineCategory category){
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = null;
        List<BudgetLine> budgetLines = null;
        try {
            transaction = session.beginTransaction();
            String hql = "FROM BudgetLine bl WHERE bl.budgetLineCategory = :category";
            budgetLines = session.createQuery(hql, BudgetLine.class)
                    .setParameter("category", category)
                    .list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
        return budgetLines;
    }

}
