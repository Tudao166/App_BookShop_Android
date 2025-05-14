package com.example.testgiuaky2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
//Lại Hoàng Phúc Khải - 22110350
public class CategoryResponse {
    // Fields from ApiResponseModels.CategoryResponse
    private boolean success;
    private String message;
    private List<Category> data;

    // Fields from the original API2 response structure
    @SerializedName("code")
    private int code;

    @SerializedName("result")
    private List<CategoryResult> result;

    // Fields for backward compatibility with the old CategoryResponse
    private String name;
    private String description;
    private String imageUrl;

    // Constructor for the old API usage - keep for backward compatibility
    public CategoryResponse(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Default constructor for Gson deserialization
    public CategoryResponse() {
    }

    // Getters and setters from ApiResponseModels.CategoryResponse
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

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
        this.data = data;
    }

    // Getters and setters for backward compatibility
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getters for the API2 response structure
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<CategoryResult> getResult() {
        return result;
    }

    public void setResult(List<CategoryResult> result) {
        this.result = result;
    }

    // Inner class for the CategoryResult from API2
    public static class CategoryResult {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("imageUrl")
        private String imageUrl;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}