package com.example.testgiuaky2.service;
import android.util.Log;

import com.example.testgiuaky2.config.RetrofitCilent;
import com.example.testgiuaky2.model.Account;
import com.example.testgiuaky2.model.AccountResponse;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//Đàng Thị Thanh Hoa_22110326

public class LoginService {
    private ApiService apiService;

    public LoginService() {
        apiService = RetrofitCilent.getRetrofitInstance().create(ApiService.class);
    }

    public void login(String email, String password, final LoginCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onError("Vui lòng nhập đủ thông tin!");
            return;
        }

        Account request = new Account(email, password);
        Call<ApiResponse<User>> call = apiService.login(request);

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
                    Log.d("LoginService", "Đăng nhập thành công!");

                } else {
                    int errorCode = response.code();
                    String errorMessage = "Đăng nhập thất bại! Mã lỗi: " + errorCode;
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Không có thông tin lỗi";
                    errorMessage += "\nChi tiết: " + errorBody;
                    callback.onError(errorMessage);
                    Log.d("LoginService", "Đăng nhập thất bại!");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
                Log.e("LoginError", t.getMessage());
            }
        });
    }

    public interface LoginCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}

