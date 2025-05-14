package com.example.testgiuaky2.service;

import android.util.Log;

import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service class to handle user-related API operations
 */
public class UserService {
    private static final String TAG = "UserService";
    private final ApiService apiService;

    public UserService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Get user information by ID
     */
    public void getUserById(String userId, final UserCallback<User> callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("ID người dùng không hợp lệ");
            return;
        }

        // Thay đổi từ Call<User> thành Call<ApiResponse<User>> để phù hợp với ApiService
        Call<ApiResponse<User>> call = apiService.getUser(userId);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lấy đối tượng User từ trường result của ApiResponse
                    User user = response.body().getResult();
                    if (user != null) {
                        callback.onSuccess(user);
                        Log.d(TAG, "Lấy thông tin người dùng thành công");
                    } else {
                        callback.onError("Không nhận được dữ liệu người dùng");
                        Log.e(TAG, "Phản hồi không có dữ liệu người dùng");
                    }
                } else {
                    int errorCode = response.code();
                    String errorMessage = "Không thể lấy thông tin người dùng. Mã lỗi: " + errorCode;
                    callback.onError(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                callback.onError(errorMessage);
                Log.e(TAG, errorMessage, t);
            }
        });
    }

    /**
     * Update user information
     */
    public void updateUser(String userId, User user, final UserCallback<User> callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onError("ID người dùng không hợp lệ");
            return;
        }

        if (user == null) {
            callback.onError("Thông tin cập nhật không hợp lệ");
            return;
        }

        Call<ApiResponse<User>> call = apiService.updateUser(userId, user);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    User updatedUser = response.body().getResult();
                    if (updatedUser != null) {
                        callback.onSuccess(updatedUser);
                        Log.d(TAG, "Cập nhật thông tin người dùng thành công");
                    } else {
                        callback.onError("Phản hồi không có dữ liệu người dùng");
                        Log.e(TAG, "Phản hồi không có dữ liệu người dùng");
                    }
                } else {
                    int errorCode = response.code();
                    String errorMessage = "Không thể cập nhật thông tin. Mã lỗi: " + errorCode;
                    callback.onError(errorMessage);
                    Log.e(TAG, errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                callback.onError(errorMessage);
                Log.e(TAG, errorMessage, t);
            }
        });
    }

    /**
     * Interface for API callbacks
     */
    public interface UserCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}