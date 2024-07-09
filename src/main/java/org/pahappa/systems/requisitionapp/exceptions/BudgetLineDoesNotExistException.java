package org.pahappa.systems.requisitionapp.exceptions;

public class BudgetLineDoesNotExistException extends RuntimeException{
    public BudgetLineDoesNotExistException(String message){
        super(message);
    }
}
