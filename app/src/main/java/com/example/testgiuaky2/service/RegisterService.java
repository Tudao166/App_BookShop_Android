package com.example.testgiuaky2.service;

import android.util.Log;

import com.example.testgiuaky2.config.RetrofitCilent;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//Nguyễn Thị Nỡ_22110392
public class RegisterService {
    private final ApiService apiService;
    public RegisterService() {
        apiService = RetrofitCilent.getRetrofitInstance().create(ApiService.class);
    }

    public void registerUser(User user, final RegisterCallback callback) {
        Call<ApiResponse<User>> call = apiService.register(user);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onFailure("Đăng ký thất bại! " + (response.body() != null ? response.body().getMessage() : "Lỗi không xác định"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi Retrofit: ", t);
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface RegisterCallback {
        void onSuccess(ApiResponse<User> user);
        void onFailure(String errorMessage);
    }
}
