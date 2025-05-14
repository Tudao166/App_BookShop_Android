package com.example.testgiuaky2.model;

public class OrderRequest {
    private String shippingAddress;
    private String phoneNumber;

    public OrderRequest(String shippingAddress, String phoneNumber) {
        this.shippingAddress = shippingAddress;
        this.phoneNumber = phoneNumber;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}