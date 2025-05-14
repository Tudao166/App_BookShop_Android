package com.example.testgiuaky2.model;

import java.util.List;

public class ApiResponseModels {

    public static class ProductResponse {
        private boolean success;
        private String message;
        private List<Product> data;

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

        public List<Product> getData() {
            return data;
        }

        public void setData(List<Product> data) {
            this.data = data;
        }
    }

    public static class CategoryResponse {
        private boolean success;
        private String message;
        private List<Category> data;

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
    }


}