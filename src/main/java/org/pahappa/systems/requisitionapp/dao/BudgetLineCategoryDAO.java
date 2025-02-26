package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BudgetLineCategoryDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public BudgetLineCategoryDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void createBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        sessionFactory.getCurrentSession().save(budgetLineCategory);
    }

    public void deleteBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        sessionFactory.getCurrentSession().delete(budgetLineCategory);
    }

    public void updateBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        sessionFactory.getCurrentSession().update(budgetLineCategory);
    }

    public BudgetLineCategory getBudgetLineCategory(int id) {
        return (BudgetLineCategory) sessionFactory.getCurrentSession().get(BudgetLineCategory.class, id);
    }

    public List<BudgetLineCategory> getAllBudgetLineCategories() {
        return sessionFactory.getCurrentSession().createCriteria(BudgetLineCategory.class).list();
    }

    public void deleteAllBudgetLineCategories() {
        sessionFactory.getCurrentSession().delete(getAllBudgetLineCategories());
    }

    public BudgetLineCategory getBudgetLineCategoryByName(String categoryName) {
        Session session = sessionFactory.getCurrentSession();
        BudgetLineCategory budgetLineCategory = null;
        try {
            String hql = "from BudgetLineCategory where categoryName = :categoryName";
            budgetLineCategory = (BudgetLineCategory) session.createQuery(hql, BudgetLineCategory.class)
                    .setParameter("categoryName", categoryName).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return budgetLineCategory;
    }

    public void refresh(BudgetLineCategory budgetLineCategory) {
        sessionFactory.getCurrentSession().refresh(budgetLineCategory);
    }

    public void detach(BudgetLineCategory budgetLineCategory) {
        sessionFactory.getCurrentSession().evict(budgetLineCategory);
    }

    public BudgetLineCategory merge(BudgetLineCategory budgetLineCategory) {
        return (BudgetLineCategory) sessionFactory.getCurrentSession().merge(budgetLineCategory);
    }

    public List<BudgetLineCategory> searchBudgetLineCategories(String searchTerm) {
        String query = "FROM BudgetLineCategory WHERE categoryName LIKE :searchTerm " ;
        return sessionFactory.getCurrentSession()
                .createQuery(query, BudgetLineCategory.class)
                .setParameter("searchTerm", "%" + searchTerm + "%")
                .getResultList();
    }

    public List<BudgetLine> getBudgetLinesForCategory(BudgetLineCategory budgetLineCategory) {
        Session session = sessionFactory.getCurrentSession();
        List<BudgetLine> budgetLines = null;
        String hql = "from BudgetLine  bl where bl.budgetLineCategory = :budgetLineCategory";
        budgetLines = session.createQuery(hql, BudgetLine.class)
                .setParameter("budgetLineCategory", budgetLineCategory)
                .list();
        return budgetLines;
    }

}
