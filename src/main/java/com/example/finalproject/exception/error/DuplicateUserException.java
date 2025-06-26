package com.example.finalproject.exception.error;

public class DuplicateUserException extends RuntimeException{

  public DuplicateUserException(String message) {
    super(message);
  }

}
