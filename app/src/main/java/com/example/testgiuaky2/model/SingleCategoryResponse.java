package com.example.testgiuaky2.model;

// This class is specifically for handling the single category response from /api/v1/categories/{id}
public class SingleCategoryResponse {
    private boolean success;
    private String message;
    private Category data;

    public SingleCategoryResponse() {
    }

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

    public Category getData() {
        return data;
    }

    public void setData(Category data) {
        this.data = data;
    }
}