package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.User;

import java.util.List;

public interface BudgetLineCategoryService {

    void createBudgetLineCategory(BudgetLineCategory budgetLineCategory);

    void deleteBudgetLineCategory(BudgetLineCategory budgetLineCategory);

    void updateBudgetLineCategory(BudgetLineCategory budgetLineCategory);

    BudgetLineCategory getBudgetLineCategory(int id);

    List<BudgetLineCategory> getAllBudgetLineCategories();

    BudgetLineCategory getBudgetLineCategoryByName(String categoryName);

    List<BudgetLineCategory> searchBudgetLineCategoriesByName(String query);

    List<BudgetLineCategory> searchBudgetLineCategories(String searchTerm);

    List<BudgetLine> getBudgetLinesForCategory(BudgetLineCategory budgetLineCategory);
}
