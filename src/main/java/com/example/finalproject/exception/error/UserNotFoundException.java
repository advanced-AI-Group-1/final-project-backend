package com.example.finalproject.exception.error;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
      super(message);
    }


}
