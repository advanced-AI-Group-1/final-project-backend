package com.example.finalproject.domain.user.dto;

public class ApiResponseDTO {

    private String status;
    private String message;

    public ApiResponseDTO() {
    }

    public ApiResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
