package com.example.testgiuaky2.model;

public class OrderStatusRequest {
    private String status;

    public OrderStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}