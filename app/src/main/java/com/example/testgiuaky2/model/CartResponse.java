package com.example.testgiuaky2.model;

public class CartResponse {
    private boolean success;
    private String message;
    private Cart data;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Cart getData() {
        return data;
    }

    public void setData(Cart data) {
        this.data = data;
    }
}