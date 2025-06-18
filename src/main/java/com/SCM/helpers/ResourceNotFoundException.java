package com.SCM.helpers;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message){
        super(message);
    }

    public ResourceNotFoundException() {
        super("User Not Found");
    }

}
