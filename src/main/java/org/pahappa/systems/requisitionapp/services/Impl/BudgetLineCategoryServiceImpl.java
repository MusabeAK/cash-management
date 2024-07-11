package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.BudgetLineCategoryDAO;
import org.pahappa.systems.requisitionapp.dao.BudgetLineDAO;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.services.BudgetLineCategoryService;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("BudgetLineCategoryService")

public class BudgetLineCategoryServiceImpl implements BudgetLineCategoryService {

    private final BudgetLineCategoryDAO budgetLineCategoryDAO;
    private final BudgetLineDAO budgetLineDAO;

    @Autowired
    public BudgetLineCategoryServiceImpl(BudgetLineCategoryDAO budgetLineCategoryDAO, BudgetLineDAO budgetLineDAO) {
        this.budgetLineCategoryDAO = budgetLineCategoryDAO;
        this.budgetLineDAO = budgetLineDAO;
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteBudgetLineCategory(BudgetLineCategory budgetLineCategory) {
        try {
            // Get a fresh instance of the entity from the database
            BudgetLineCategory managedCategory = budgetLineCategoryDAO.getBudgetLineCategory(budgetLineCategory.getId());
            if (managedCategory == null) {
                throw new RuntimeException("Budget Line Category Not Found.");
            }
            List<BudgetLine> budgetLines = managedCategory.getBudgetLines();
            for (BudgetLine budgetLine : budgetLines) {
                budgetLineDAO.deleteBudgetLine(budgetLine);
            }
            budgetLineCategoryDAO.deleteBudgetLineCategory(managedCategory);
        } catch (Exception e) {
            throw new RuntimeException("Budget Line Category Not Deleted: " + e.getMessage());
        }
    }

    @Override
    @Transactional
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
    @Transactional
    public BudgetLineCategory getBudgetLineCategory(int id){
        BudgetLineCategory budgetLineCategory = budgetLineCategoryDAO.getBudgetLineCategory(id);
        if (budgetLineCategory == null) {
            throw new RuntimeException("Budget Line Category Not Found.");
        }
        return budgetLineCategoryDAO.getBudgetLineCategory(id);
    }

    @Override
    @Transactional
    public List<BudgetLineCategory> getAllBudgetLineCategories(){
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        if (budgetLineCategories.isEmpty()) {
            return Collections.emptyList();
        }
        return budgetLineCategoryDAO.getAllBudgetLineCategories();
    }

    @Override
    @Transactional
    public BudgetLineCategory getBudgetLineCategoryByName(String categoryName) {
        BudgetLineCategory budgetLineCategory = budgetLineCategoryDAO.getBudgetLineCategoryByName(categoryName);
        if (budgetLineCategory == null) {
            throw new RuntimeException("Budget Line Category Not Found.");
        }
        budgetLineCategoryDAO.refresh(budgetLineCategory);
        return budgetLineCategory;
    }

    @Override
    @Transactional
    public List<BudgetLineCategory> searchBudgetLineCategoriesByName(String query){
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        if (query == null || query.trim().isEmpty()) {
            return budgetLineCategories;
        }
        return budgetLineCategories.stream()
                .filter(budgetLineCategory -> budgetLineCategory.getCategoryName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

}
