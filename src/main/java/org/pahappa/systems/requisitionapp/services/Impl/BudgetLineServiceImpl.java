package org.pahappa.systems.requisitionapp.services.Impl;

import org.pahappa.systems.requisitionapp.dao.BudgetLineCategoryDAO;
import org.pahappa.systems.requisitionapp.dao.BudgetLineDAO;
import org.pahappa.systems.requisitionapp.dao.RequisitionDAO;
import org.pahappa.systems.requisitionapp.models.BudgetLine;
import org.pahappa.systems.requisitionapp.models.BudgetLineCategory;
import org.pahappa.systems.requisitionapp.models.Requisition;
import org.pahappa.systems.requisitionapp.models.utils.BudgetLineStatus;
import org.pahappa.systems.requisitionapp.services.BudgetLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service("BudgetLineService")

public class BudgetLineServiceImpl implements BudgetLineService {

    private final BudgetLineDAO budgetLineDAO;
    private final BudgetLineCategoryDAO budgetLineCategoryDAO;

    @Autowired
    public BudgetLineServiceImpl(BudgetLineDAO budgetLineDAO, BudgetLineCategoryDAO budgetLineCategoryDAO) {
        this.budgetLineDAO = budgetLineDAO;
        this.budgetLineCategoryDAO = budgetLineCategoryDAO;
    }

    @Override
    @Transactional
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

        budgetLineCategoryDAO.detach(budgetLineCategory);
        budgetLineCategory = budgetLineCategoryDAO.merge(budgetLineCategory);

        budgetLine.setStatus(BudgetLineStatus.DRAFT);
        budgetLine.setBalance(budgetLine.getInitialAmount());
        budgetLine.setFloatAmount(budgetLine.getBalance());
        budgetLineDAO.addBudgetLineToBudgetLineCategory(budgetLine, budgetLineCategory);
    }

    @Override
    @Transactional
    public void updateBudgetLine(BudgetLine budgetLine){
        BudgetLine budgetLineToUpdate = budgetLineDAO.getBudgetLineById(budgetLine.getId());
        List<BudgetLineCategory> budgetLineCategories = budgetLineCategoryDAO.getAllBudgetLineCategories();
        if(budgetLineToUpdate == null){
            throw new RuntimeException("No budget line found");
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
        if(budgetLine.getEndDate().before(new Date())){
            throw new ArithmeticException("End date cannot be before current date");
        }
        if(budgetLine.getTitle().trim().isEmpty()){
            throw new RuntimeException("Title cannot be empty");
        }

        budgetLineToUpdate.setStatus(budgetLine.getStatus());
        budgetLineToUpdate.setInitialAmount(budgetLine.getInitialAmount());
        budgetLineToUpdate.setBalance(budgetLine.getBalance());
        budgetLineToUpdate.setFloatAmount(budgetLine.getFloatAmount());
        budgetLineToUpdate.setTitle(budgetLine.getTitle());
        budgetLineToUpdate.setEndDate(budgetLine.getEndDate());
        budgetLineToUpdate.setStartDate(budgetLine.getStartDate());
        budgetLineDAO.updateBudgetLine(budgetLineToUpdate);
    }

    @Override
    @Transactional
    public void deleteBudgetLine(BudgetLine budgetLine){
        BudgetLine managedBudgetLine = budgetLineDAO.getBudgetLineById(budgetLine.getId());
        if(managedBudgetLine == null){
            throw new RuntimeException("No budget line found");
        }
        BudgetLineCategory category = managedBudgetLine.getBudgetLineCategory();
        if (category != null) {
            category.getBudgetLines().remove(managedBudgetLine);
            budgetLineCategoryDAO.updateBudgetLineCategory(category);
        }
        if (managedBudgetLine.getRequisitions() != null) {
            for (Requisition requisition : managedBudgetLine.getRequisitions()) {
                requisition.setBudgetLine(null);
                // You might need to update the requisition in the database here
                // requisitionDAO.updateRequisition(requisition);
            }
        }
        budgetLineDAO.deleteBudgetLine(managedBudgetLine);
    }

    @Override
    @Transactional
    public List<BudgetLine> getAllBudgetLines(){
        List<BudgetLine> budgetLines = budgetLineDAO.getAllBudgetLines();
        if(budgetLines.isEmpty()){
            return Collections.emptyList();
        }
        return budgetLineDAO.getAllBudgetLines();
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public BudgetLine getBudgetLineByTitle(String title){
        if(title == null || title.trim().isEmpty()){
            throw new RuntimeException("Title cannot be empty");
        }
        BudgetLine budgetLine = budgetLineDAO.getBudgetLineByTitle(title);
        if(budgetLine == null){
            throw new RuntimeException("No budget line found");
        }
        return budgetLine;
    }

    public List<BudgetLine> searchBudgetLines(String searchTerm) {
        return budgetLineDAO.searchBudgetLines(searchTerm);
    }


    @Override
    @Transactional
    public int getActiveBudgetLineCount(){
        List<BudgetLine> budgetLines = budgetLineDAO.getAllBudgetLines();
        List<BudgetLine> activeBudgetLines = new ArrayList<>();
        Date currentDate = new Date();
        for (BudgetLine budgetLine : budgetLines) {
            if (currentDate.after(budgetLine.getEndDate())){
                budgetLine.setStatus(BudgetLineStatus.EXPIRED);
            }
            if (budgetLine.getStatus().equals(BudgetLineStatus.APPROVED)){
                activeBudgetLines.add(budgetLine);
            }
        }
        return activeBudgetLines.size();
    }

    @Override
    @Transactional
    public List<Requisition> getBudgetLineRequisitions(long budgetLineId){
        List<Requisition> requisitions = budgetLineDAO.getBudgetLineRequisitions(budgetLineId);
        if (requisitions.isEmpty()){
            return Collections.emptyList();
        }
        return requisitions;
    }

    @Override
    @Transactional
    public List<BudgetLine> filterBudgetLineByCategory(BudgetLineCategory budgetLineCategory){
        List<BudgetLine> budgetLines = budgetLineDAO.filterBudgetLineByCategory(budgetLineCategory);
        if (budgetLines.isEmpty()){
            return Collections.emptyList();
        }
        return budgetLines;
    }

    @Override
    @Transactional
    public List<BudgetLine> filterBudgetLineByStatus(String status){
        List<BudgetLine> budgetLines = budgetLineDAO.filterBudgetLineByStatus(status);
        if (budgetLines.isEmpty()){
            return Collections.emptyList();
        }
        return budgetLines;
    }

    @Override
    @Transactional
    public double getBudgetLineTotalAmount(){
        List<BudgetLine> budgetLines = budgetLineDAO.getAllBudgetLines();
        List<BudgetLine> activeBudgetLines = new ArrayList<>();
        Date currentDate = new Date();
        for (BudgetLine budgetLine : budgetLines) {
            if (currentDate.after(budgetLine.getEndDate())){
                budgetLine.setStatus(BudgetLineStatus.EXPIRED);
            }
            if (budgetLine.getStatus().equals(BudgetLineStatus.APPROVED)){
                activeBudgetLines.add(budgetLine);
            }
        }
        return activeBudgetLines.stream().mapToInt(BudgetLine::getBalance).sum();

    }
}
