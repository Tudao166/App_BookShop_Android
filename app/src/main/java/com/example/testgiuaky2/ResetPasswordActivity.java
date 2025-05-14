package com.example.testgiuaky2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testgiuaky2.config.RetrofitCilent;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.ResetPasswordRequest;
import com.example.testgiuaky2.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtOtp, edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;
    private ImageView btnBack;
    private String email;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Get email from intent
        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Lỗi: Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        edtOtp = findViewById(R.id.edtOtp);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        // Initialize API service
        apiService = RetrofitCilent.getRetrofitInstance().create(ApiService.class);

        // Set click listener for back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set click listener for reset password button
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String otp = edtOtp.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        // progressBar.setVisibility(View.VISIBLE);

        // Create request
        ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);

        // Call API
        apiService.resetPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Hide loading indicator
                // progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ResetPasswordActivity.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_LONG).show();

                    // Navigate to login screen
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    int errorCode = response.code();
                    String errorMessage = "Không thể đặt lại mật khẩu. Mã lỗi: " + errorCode;
                    Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Đặt lại mật khẩu thành công!", Toast.LENGTH_LONG).show();

                // Navigate to login screen
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}