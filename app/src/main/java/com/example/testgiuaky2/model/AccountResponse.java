package com.example.testgiuaky2.model;
//Nguyễn Thị Hồng Hà - 22110001
public class AccountResponse {
    private int code;
    private User result;

    public AccountResponse() {
    }
    public AccountResponse(int code, User result) {
        this.code = code;
        this.result = result;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(User result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public User getResult() {
        return result;
    }
}
