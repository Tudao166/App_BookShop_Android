package com.example.testgiuaky2.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Review {
    private int id;
    private String content;
    private int rating;
    private Product product;
    private User user;
    private Date createdAt;
    private Date updatedAt;

    public Review() {
    }

    // Constructor for creating a new review
    public Review(String content, int rating, int productId, String userId) {
        this.content = content;
        this.rating = rating;
        this.product = new Product();
        this.product.setId(productId);
        this.user = new User();
        this.user.setId(userId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}