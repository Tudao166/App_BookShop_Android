package com.example.testgiuaky2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.testgiuaky2.config.PrefManager;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.User;
import com.example.testgiuaky2.service.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // Views
    private ImageView ivProfilePicture;
    private TextInputEditText etEmail, etFullName, etImageUrl;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave, btnPreviewImage;
    private ProgressBar progressBar;

    // Data
    private User currentUser;
    private ApiService apiService;
    private PrefManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize PrefManager to get userId
        prefManager = new PrefManager(requireContext());

        // Set up listeners
        setupListeners();

        // Debug information
        debugPreferences();

        // Load user data
        loadUserData();
    }

    private void debugPreferences() {
        Log.d(TAG, "Debug info - User ID: " + prefManager.getUserId());
        Log.d(TAG, "Debug info - Email: " + prefManager.getEmail());
        Log.d(TAG, "Debug info - Name: " + prefManager.getFullName());
        Log.d(TAG, "Debug info - Is Logged In: " + prefManager.isLoggedIn());
    }

    private void initViews(View view) {
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        etEmail = view.findViewById(R.id.et_email);
        etFullName = view.findViewById(R.id.et_full_name);
        etImageUrl = view.findViewById(R.id.et_image_url);
        rgGender = view.findViewById(R.id.rg_gender);
        rbMale = view.findViewById(R.id.rb_male);
        rbFemale = view.findViewById(R.id.rb_female);
        btnSave = view.findViewById(R.id.btn_save);
        btnPreviewImage = view.findViewById(R.id.btn_preview_image);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        // Add preview button listener
        btnPreviewImage.setOnClickListener(v -> {
            String imageUrl = etImageUrl.getText().toString().trim();
            if (!imageUrl.isEmpty()) {
                if (Patterns.WEB_URL.matcher(imageUrl).matches()) {
                    loadImageFromUrl(imageUrl);
                } else {
                    showToast("URL ảnh không hợp lệ");
                }
            } else {
                showToast("Vui lòng nhập URL ảnh");
            }
        });

        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                updateUserInfo();
            }
        });
    }

    private void loadImageFromUrl(String url) {
        try {
            showToast("Đang tải ảnh...");

            // Log the image URL for debugging
            Log.d(TAG, "Loading image from URL: " + url);

            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e,
                                                    Object model,
                                                    com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                    boolean isFirstResource) {
                            Log.e(TAG, "Image load failed for URL: " + url, e);
                            showToast("Không thể tải ảnh, vui lòng kiểm tra URL");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource,
                                                       Object model,
                                                       com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource,
                                                       boolean isFirstResource) {
                            showToast("Tải ảnh thành công");
                            return false;
                        }
                    })
                    .into(ivProfilePicture);
        } catch (Exception e) {
            Log.e(TAG, "Exception loading image", e);
            showToast("Lỗi khi tải ảnh: " + e.getMessage());
        }
    }

    private void loadUserData() {
        showLoading(true);

        // Get user ID from PrefManager
        String userId = prefManager.getUserId();

        if (userId != null && !userId.isEmpty()) {
            Log.d(TAG, "Loading user data for user ID: " + userId);
            Call<ApiResponse<User>> call = apiService.getUser(userId);
            call.enqueue(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        // Lấy user từ trường result của ApiResponse
                        currentUser = response.body().getResult();
                        displayUserInfo();
                        Log.d(TAG, "User data loaded successfully");
                    } else {
                        showToast("Không thể tải thông tin người dùng: " + response.code());
                        Log.e(TAG, "Error loading user: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    showLoading(false);
                    showToast("Lỗi kết nối: " + t.getMessage());
                    Log.e(TAG, "Connection error: ", t);
                }
            });
        } else {
            showLoading(false);
            showToast("Không tìm thấy thông tin đăng nhập");
            Log.e(TAG, "User ID is null or empty");

            // Redirect to login if not logged in
            if (!prefManager.isLoggedIn()) {
                Log.d(TAG, "User not logged in, redirecting to login screen");
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        }
    }

    private void displayUserInfo() {
        if (currentUser != null) {
            // Set text fields
            etEmail.setText(currentUser.getEmail());
            etFullName.setText(currentUser.getFullName());

            // Set image URL field
            if (currentUser.getPicture() != null && !currentUser.getPicture().isEmpty()) {
                etImageUrl.setText(currentUser.getPicture());
            }

            // Set gender selection
            if (currentUser.isGender()) {
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }

            // Load profile picture
            if (currentUser.getPicture() != null && !currentUser.getPicture().isEmpty()) {
                loadImageFromUrl(currentUser.getPicture());
            }
        }
    }

    private boolean validateInputs() {
        String fullName = etFullName.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (!imageUrl.isEmpty() && !Patterns.WEB_URL.matcher(imageUrl).matches()) {
            etImageUrl.setError("URL ảnh không hợp lệ");
            return false;
        }

        return true;
    }

    private void updateUserInfo() {
        showLoading(true);

        // Get user ID from PrefManager
        String userId = prefManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            showLoading(false);
            showToast("Không tìm thấy thông tin đăng nhập");
            return;
        }

        // Prepare updated user data
        User updatedUser = new User();
        updatedUser.setFullName(etFullName.getText().toString().trim());
        updatedUser.setGender(rbMale.isChecked());

        // Set picture URL from input field
        String imageUrl = etImageUrl.getText().toString().trim();
        if (!imageUrl.isEmpty()) {
            // Log the URL we're trying to save
            Log.d(TAG, "Updating profile with image URL: " + imageUrl);
            updatedUser.setPicture(imageUrl);
        } else if (currentUser != null) {
            // Keep current picture if not changed
            updatedUser.setPicture(currentUser.getPicture());
        }

        // Call API to update user
        Call<ApiResponse<User>> call = apiService.updateUser(userId, updatedUser);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    showToast("Cập nhật thông tin thành công");
                    currentUser = response.body().getResult();
                    displayUserInfo(); // Refresh display with updated info
                } else {
                    int errorCode = response.code();
                    showToast("Không thể cập nhật thông tin: " + errorCode);
                    Log.e(TAG, "Error updating user: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                showLoading(false);
                showToast("Lỗi kết nối: " + t.getMessage());
                Log.e(TAG, "Connection error: ", t);
            }
        });
    }

    private void showLoading(boolean isVisible) {
        if (progressBar != null) {
            progressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!isVisible);
            etImageUrl.setEnabled(!isVisible);
            btnPreviewImage.setEnabled(!isVisible);
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}