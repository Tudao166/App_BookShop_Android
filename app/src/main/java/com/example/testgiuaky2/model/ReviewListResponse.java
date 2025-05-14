package com.example.testgiuaky2.model;

public class ReviewListResponse {
    private boolean success;
    private String message;
    private Review[] data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Review[] getData() {
        return data;
    }
}
