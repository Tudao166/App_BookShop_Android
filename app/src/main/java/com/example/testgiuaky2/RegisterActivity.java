package com.example.testgiuaky2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.User;
import com.example.testgiuaky2.service.RegisterService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

// Nguyễn Thị Nỡ_22110392
public class RegisterActivity extends AppCompatActivity {

    private TextView tvLogin;
    private TextInputEditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private RadioGroup radioGender;
    private MaterialButton btnRegister;
    private RegisterService registerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        // Initialize UI components
        initializeUI();

        // Set click listeners
        setupClickListeners();

        // Initialize service
        registerService = new RegisterService();
    }

    private void initializeUI() {
        // TextViews
        tvLogin = findViewById(R.id.tvLogin);

        // EditTexts - Using TextInputEditText
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        // RadioGroup
        radioGender = findViewById(R.id.radioGender);

        // Buttons
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void setupClickListeners() {
        // Navigate to Login screen
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate form fields
        if (!validateFormFields(name, email, password, confirmPassword)) {
            return;
        }

        // Get gender selection
        int selectedGenderId = radioGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isFemale = (selectedGenderId == R.id.rbFemale);

        // Create user object
        User user = new User(name, password, email, isFemale);

        // Show loading state
        btnRegister.setEnabled(false);
        btnRegister.setText("Processing...");

        // Call registration service
        registerService.registerUser(user, new RegisterService.RegisterCallback() {
            @Override
            public void onSuccess(ApiResponse<User> response) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("SIGN UP");

                    if (response.getResult() == null) {
                        Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to OTP verification
                        Intent intent = new Intent(RegisterActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("SIGN UP");
                    Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean validateFormFields(String name, String email, String password, String confirmPassword) {
        // Check for empty fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check password length
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}