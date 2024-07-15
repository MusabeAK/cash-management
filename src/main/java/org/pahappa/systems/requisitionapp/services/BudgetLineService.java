package org.pahappa.systems.requisitionapp.services;

import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.User;

import java.util.List;

public interface BudgetLineService {

    void createBudgetLine(BudgetLine budgetLine, BudgetLineCategory budgetLineCategory);

    void updateBudgetLine(BudgetLine budgetLine);

    void deleteBudgetLine(BudgetLine budgetLine);

    List<BudgetLine> getAllBudgetLines();

    BudgetLine getBudgetLineById(long id);

    BudgetLine getBudgetLineByTitle(String title);

    List<BudgetLine> searchBudgetLines(String searchTerm);
}
