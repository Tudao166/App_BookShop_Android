package com.example.testgiuaky2.model;

import com.example.testgiuaky2.model.Product;

public class ProductDetailResponse {
    private boolean success;
    private String message;
    private Product data;

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

    public Product getData() {
        return data;
    }

    public void setData(Product data) {
        this.data = data;
    }
}