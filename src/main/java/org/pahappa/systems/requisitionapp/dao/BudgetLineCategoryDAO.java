package org.pahappa.systems.requisitionapp.dao;

import org.hibernate.SessionFactory;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BudgetLineCategoryDAO {

    private final SessionFactory sessionFactory;

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

    public BudgetLineCategory getBudgetLineCategory(long id) {
        return (BudgetLineCategory) sessionFactory.getCurrentSession().get(BudgetLineCategory.class, id);
    }

    public List<BudgetLineCategory> getAllBudgetLineCategories() {
        return sessionFactory.getCurrentSession().createCriteria(BudgetLineCategory.class).list();
    }

    public void deleteAllBudgetLineCategories() {
        sessionFactory.getCurrentSession().delete(getAllBudgetLineCategories());
    }

}
