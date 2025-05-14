package com.example.testgiuaky2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testgiuaky2.config.RetrofitCilent;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.OtpRequest;
import com.example.testgiuaky2.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Nguyễn Thị Nỡ_22110392
public class VerifyOtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerify;
    private String userEmail;

    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        apiService = RetrofitCilent.getRetrofitInstance().create(ApiService.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.verify);

        userEmail = getIntent().getStringExtra("email");

        btnVerify.setOnClickListener(v -> {
            Log.d("VerifyOtp", "Button clicked");

            String otp = etOtp.getText().toString().trim();

            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            OtpRequest otpRequest = new OtpRequest(userEmail, otp);


            apiService.verifyOtp(otpRequest).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.code() == 200) {
                            Toast.makeText(VerifyOtpActivity.this, "OTP verified successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(VerifyOtpActivity.this, LoginActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(VerifyOtpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(VerifyOtpActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void resendOtp() {
        Toast.makeText(this, "OTP resent!", Toast.LENGTH_SHORT).show();
    }
}
