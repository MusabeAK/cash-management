package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.BudgetLineCategoryDAO;
import org.pahappa.systems.requisitionapp.dao.BudgetLineDAO;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("BudgetLineService")
@Transactional
public class BudgetLineServiceImpl implements BudgetLineService {

    private final BudgetLineDAO budgetLineDAO;
    private final BudgetLineCategoryDAO budgetLineCategoryDAO;

    @Autowired
    public BudgetLineServiceImpl(BudgetLineDAO budgetLineDAO, BudgetLineCategoryDAO budgetLineCategoryDAO) {
        this.budgetLineDAO = budgetLineDAO;
        this.budgetLineCategoryDAO = budgetLineCategoryDAO;
    }

    @Override
    public void createBudgetLine(BudgetLine budgetLine, BudgetLineCategory budgetLineCategory){
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        if(budgetLineCategories.isEmpty()){
            throw new RuntimeException("No budget line category found");
        }
        if(!budgetLineCategories.contains(budgetLineCategory)){
            throw new RuntimeException("Budget Line Category does not exist");
        }
        if(budgetLine.getInitialAmount() <= 0){
            throw new ArithmeticException("Initial amount cannot be negative");
        }
        if(budgetLine.getEndDate().before(budgetLine.getStartDate())){
            throw new ArithmeticException("End date cannot be before start date");
        }
        if(budgetLine.getStartDate().after(budgetLine.getEndDate())){
            throw new ArithmeticException("Start date cannot be after end date");
        }
        if(budgetLine.getTitle().trim().isEmpty()){
            throw new RuntimeException("Title cannot be empty");
        }
        budgetLine.setStatus(BudgetLineStatus.DRAFT);
        budgetLine.setBalance(budgetLine.getInitialAmount());
        budgetLineDAO.addBudgetLineToBudgetLineCategory(budgetLine, budgetLineCategory);
    }

    @Override
    public void updateBudgetLine(BudgetLine budgetLine){
        BudgetLine budgetLineToUpdate = budgetLineDAO.getBudgetLineById(budgetLine.getId());
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        if(budgetLineToUpdate == null){
            throw new RuntimeException("No budget line found");
        }
        if(budgetLineToUpdate.getStatus() != BudgetLineStatus.DRAFT){
            throw new RuntimeException("Budget Line is not in draft");
        }
        if(budgetLineCategories.isEmpty()){
            throw new RuntimeException("No budget line category found");
        }
        if(budgetLine.getInitialAmount() <= 0){
            throw new ArithmeticException("Initial amount cannot be negative");
        }
        if(budgetLine.getEndDate().before(budgetLine.getStartDate())){
            throw new ArithmeticException("End date cannot be before start date");
        }
        if(budgetLine.getStartDate().after(budgetLine.getEndDate())){
            throw new ArithmeticException("Start date cannot be after end date");
        }
        if(budgetLine.getTitle().trim().isEmpty()){
            throw new RuntimeException("Title cannot be empty");
        }

        budgetLineToUpdate.setStatus(budgetLine.getStatus());
        budgetLineToUpdate.setInitialAmount(budgetLine.getInitialAmount());
        budgetLineToUpdate.setBalance(budgetLine.getBalance());
        budgetLineToUpdate.setTitle(budgetLine.getTitle());
        budgetLineToUpdate.setEndDate(budgetLine.getEndDate());
        budgetLineToUpdate.setStartDate(budgetLine.getStartDate());
        budgetLineDAO.updateBudgetLine(budgetLineToUpdate);
    }

    @Override
    public void deleteBudgetLine(BudgetLine budgetLine){
        BudgetLine existingBudgetLine = budgetLineDAO.getBudgetLineById(budgetLine.getId());
        if(existingBudgetLine == null){
            throw new RuntimeException("No budget line found");
        }
        budgetLineDAO.deleteBudgetLine(existingBudgetLine);
    }

    @Override
    public List<BudgetLine> getAllBudgetLines(){
        List<BudgetLine> budgetLines = budgetLineDAO.getAllBudgetLines();
        if(budgetLines.isEmpty()){
            throw new RuntimeException("No budget line found");
        }
        return budgetLineDAO.getAllBudgetLines();
    }

    @Override
    public BudgetLine getBudgetLineById(long id){
        if(id <= 0){
            throw new RuntimeException("ID cannot be negative");
        }
        BudgetLine budgetLine = budgetLineDAO.getBudgetLineById(id);
        if(budgetLine == null){
            throw new RuntimeException("No budget line found");
        }
        return budgetLineDAO.getBudgetLineById(id);
    }


}
