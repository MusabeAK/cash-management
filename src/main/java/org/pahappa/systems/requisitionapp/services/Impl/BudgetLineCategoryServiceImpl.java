package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.BudgetLineCategoryDAO;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.services.BudgetLineCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("BudgetLineCategoryService")
@Transactional
public class BudgetLineCategoryServiceImpl implements BudgetLineCategoryService {

    private final BudgetLineCategoryDAO budgetLineCategoryDAO;

    @Autowired
    public BudgetLineCategoryServiceImpl(BudgetLineCategoryDAO budgetLineCategoryDAO) {
        this.budgetLineCategoryDAO = budgetLineCategoryDAO;
    }

    public void createBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        Optional<BudgetLineCategory> optionalBudgetLineCategory = budgetLineCategories
                                                                    .stream()
                                                                    .filter(budgetLineCategory1 -> budgetLineCategory1.getCategoryName().equals(budgetLineCategory.getCategoryName()))
                                                                    .findAny();
        if (optionalBudgetLineCategory.isPresent()) {
            throw new RuntimeException("Budget Line Category with the same Name Already Created.");
        }
        if (budgetLineCategory.getCategoryName().trim().isEmpty()){
            throw new RuntimeException("Budget Line Category Name Empty.");
        }
        try {
            budgetLineCategoryDAO.createBudgetLineCategory(budgetLineCategory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        Optional<BudgetLineCategory> optionalBudgetLineCategory = budgetLineCategories
                .stream()
                .filter(budgetLineCategory1 -> budgetLineCategory1.getId() == budgetLineCategory.getId())
                .findAny();
        if (!optionalBudgetLineCategory.isPresent()) {
            throw new RuntimeException("Budget Line Category Not Found.");
        }
        try {
            budgetLineCategoryDAO.deleteBudgetLineCategory(budgetLineCategory);
        } catch (Exception e) {
            throw new RuntimeException("Budget Line Category Not Deleted: " + e.getMessage() );
        }
    }

    @Override
    public void updateBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        BudgetLineCategory budgetLineCategoryToUpdate = budgetLineCategoryDAO.getBudgetLineCategory(budgetLineCategory.getId());
        if (budgetLineCategoryToUpdate == null) {
            throw new RuntimeException("Budget Line Category Not Found.");
        }
        if (budgetLineCategory.getCategoryName().trim().isEmpty()){
            throw new RuntimeException("New Budget Line Category Name Empty.");
        }
        if (budgetLineCategoryToUpdate.getCategoryName().equals(budgetLineCategory.getCategoryName())) {
            throw new RuntimeException("Budget Line Category Name cannot be the same as before.");
        }
        budgetLineCategoryToUpdate.setCategoryName(budgetLineCategory.getCategoryName());
        budgetLineCategoryDAO.updateBudgetLineCategory(budgetLineCategoryToUpdate);
    }

    @Override
    public BudgetLineCategory getBudgetLineCategory(int id){
        BudgetLineCategory budgetLineCategory = budgetLineCategoryDAO.getBudgetLineCategory(id);
        if (budgetLineCategory == null) {
            throw new RuntimeException("Budget Line Category Not Found.");
        }
        return budgetLineCategoryDAO.getBudgetLineCategory(id);
    }

    @Override
    public List<BudgetLineCategory> getAllBudgetLineCategories(){
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        if (budgetLineCategories.isEmpty()) {
            throw new RuntimeException("No Budget Line Category Found.");
        }
        return budgetLineCategoryDAO.getAllBudgetLineCategories();
    }

}
