package org.pahappa.systems.requisitionapp.exceptions;

public class RequisitionNotFoundException extends RuntimeException{
    public RequisitionNotFoundException(String message){
        super(message);
    }
}
