package com.example.testgiuaky2;

//Đào Thanh Tú - 22110452

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testgiuaky2.config.PrefManager;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.model.Account;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.User;
import com.example.testgiuaky2.service.ApiService;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton btnLogin; // Thay đổi từ ImageView thành MaterialButton
    private EditText edtEmail, edtPass;
    private ApiService apiService;
    private TextView tvForgotPassword;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // Đảm bảo tên file layout đúng

        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Find and set click listener for forgot password TextView
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        apiService = ApiClient.getClient().create(ApiService.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập email và mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d(TAG, "Gửi yêu cầu đăng nhập...");

                Account request = new Account(email, password);
                apiService.login(request).enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User user = response.body().getResult();

                            // Save all necessary user information, including ID
                            PrefManager prefManager = new PrefManager(LoginActivity.this);
                            prefManager.saveLoginDetails(
                                    user.getId(), // Save user ID
                                    user.getFullName(),
                                    user.getEmail(),
                                    user.getPicture()
                            );

                            Log.d(TAG, "Đăng nhập thành công! User ID: " + user.getId());
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            int errorCode = response.code();
                            String errorMessage = "Đăng nhập thất bại! Mã lỗi: " + errorCode;
                            String errorBody = response.errorBody() != null ? response.errorBody().toString() : "Không có thông tin lỗi";
                            errorMessage += "\nChi tiết: " + errorBody;

                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(TAG, errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                    }
                });
            }
        });
    }
}