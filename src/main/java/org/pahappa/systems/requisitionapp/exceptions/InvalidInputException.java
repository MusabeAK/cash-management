package org.pahappa.systems.requisitionapp.exceptions;

public class InvalidInputException extends IllegalArgumentException{
    public InvalidInputException(String message){
        super(message);
    }
}
