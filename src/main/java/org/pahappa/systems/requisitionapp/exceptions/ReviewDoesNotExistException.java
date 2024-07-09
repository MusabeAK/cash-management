package org.pahappa.systems.requisitionapp.exceptions;

public class ReviewDoesNotExistException extends RuntimeException{
    public ReviewDoesNotExistException(String message){
        super(message);
    }
}
