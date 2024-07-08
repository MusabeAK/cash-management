package org.pahappa.systems.requisitionapp.exceptions;

public class UserDoesNotExistException extends Exception{
    public UserDoesNotExistException(String message){
        super(message);
    }
}
