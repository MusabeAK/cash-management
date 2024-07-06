package org.pahappa.systems.requisitionapp.services;

public interface UserService {
    void reviewBudgetLine();
    void reviewRequest();
    void rejectRequest();
    void approveRequest();
    void approveBudgetLine();
    void viewAccountability();
    void createBudgetLine();
    void viewBudgetLines();
    void viewRequests();
    void viewUsers();
    void disburseMoney();
}
