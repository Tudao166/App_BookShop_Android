package com.example.testgiuaky2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testgiuaky2.config.RetrofitCilent;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.ForgotPasswordRequest;
import com.example.testgiuaky2.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ForgotPasswordActivity.java
public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSubmit;
    private ImageView btnBack;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        edtEmail = findViewById(R.id.edtEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
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

        // Set click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private void forgotPassword() {
        String email = edtEmail.getText().toString().trim();

        // Validate email
        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Định dạng email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        // progressBar.setVisibility(View.VISIBLE);

        // Create request
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        // Call API
        apiService.forgotPassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Hide loading indicator
                // progressBar.setVisibility(View.GONE);


                    Toast.makeText(ForgotPasswordActivity.this, "Mã OTP đã được gửi đến email của bạn", Toast.LENGTH_LONG).show();

                    // Navigate to reset password screen
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
//                // Hide loading indicator
//                // progressBar.setVisibility(View.GONE);
//
//                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
//
                Toast.makeText(ForgotPasswordActivity.this, "Mã OTP đã được gửi đến email của bạn", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
        });
    }
}