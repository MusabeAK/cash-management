package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BudgetLineDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public BudgetLineDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addBudgetLineToBudgetLineCategory(BudgetLine budgetLine, BudgetLineCategory budgetLineCategory) {
        Session session = sessionFactory.getCurrentSession();
        budgetLine.setBudgetLineCategory(budgetLineCategory);
        budgetLineCategory.getBudgetLines().add(budgetLine);
        session.saveOrUpdate(budgetLine);
        session.saveOrUpdate(budgetLineCategory);
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
        List<BudgetLine> budgetLines = null;
        String hql = "FROM BudgetLine bl WHERE bl.budgetLineCategory = :category";
        budgetLines = session.createQuery(hql, BudgetLine.class)
                .setParameter("category", category)
                .list();
        return budgetLines;
    }

    public BudgetLine getBudgetLineByTitle(String title) {
        Session session = sessionFactory.getCurrentSession();
        BudgetLine budgetLine = null;
        try {
            String hql = "FROM BudgetLine bl WHERE bl.title = :title";
            budgetLine = (BudgetLine) session.createQuery(hql, BudgetLine.class)
                    .setParameter("title", title)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return budgetLine;
    }

    public List<BudgetLine> searchBudgetLines(String searchTerm) {
        String query = "FROM BudgetLine WHERE comment LIKE :searchTerm " +
                "OR title LIKE :searchTerm ";
        return sessionFactory.getCurrentSession()
                .createQuery(query, BudgetLine.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .getResultList();
    }

    public List<Requisition> getBudgetLineRequisitions(long budgetLineId) {
        Session session = sessionFactory.getCurrentSession();
        List<Requisition> requisitions = null;
        try {
            String hql = "FROM Requisition r WHERE r.budgetLine.id = :budgetLineId";
            requisitions = session.createQuery(hql, Requisition.class)
                    .setParameter("budgetLineId", budgetLineId)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requisitions;
    }

}
