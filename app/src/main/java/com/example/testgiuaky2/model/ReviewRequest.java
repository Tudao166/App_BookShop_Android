package com.example.testgiuaky2.model;

public class ReviewRequest {
    private String content;
    private int rating;
    private int productId;
    private String userId;

    public ReviewRequest(String content, int rating, int productId, String userId) {
        this.content = content;
        this.rating = rating;
        this.productId = productId;
        this.userId = userId;
    }
}