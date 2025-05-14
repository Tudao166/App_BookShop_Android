package com.example.testgiuaky2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testgiuaky2.adapter.OrderAdapter;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.config.PrefManager;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.Order;
import com.example.testgiuaky2.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFragment extends Fragment {
    private static final String TAG = "OrderListFragment";
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private ProgressBar progressBar;
    private TextView tvEmptyOrders;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private String userId;

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyOrders = view.findViewById(R.id.tvEmptyOrders);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Get user ID from shared preferences
        PrefManager prefManager = new PrefManager(requireContext());
        userId = prefManager.getUserId();

        // Debug log to verify userId
        Log.d(TAG, "User ID from PrefManager: " + userId);

        // Initialize order list and adapter
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(orderAdapter);

        // Set up swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);

        // Load orders initially
        loadOrders();
    }

    private void loadOrders() {
        showLoading(true);

        // Check if user is logged in
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty");
            showEmptyView("Vui lòng đăng nhập để xem đơn hàng");
            showLoading(false);
            return;
        }

        Log.d(TAG, "Making API call to get orders for user: " + userId);

        // Make API call to get user orders
        apiService.getUserOrders(userId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API call successful. Response code: " + response.code());
                    ApiResponse<List<Order>> apiResponse = response.body();
                    Log.d(TAG, "API Response success: " + apiResponse.isSuccess());

                    // FIX: Get orders from getData() instead of getResult()
                    List<Order> orders = apiResponse.getData();

                    // Fallback to getResult() if getData() returns null
                    if (orders == null) {
                        orders = apiResponse.getResult();
                        Log.d(TAG, "Using getResult() because getData() returned null");
                    }

                    if (orders != null) {
                        Log.d(TAG, "Received orders list. Size: " + orders.size());
                    } else {
                        Log.d(TAG, "Received null orders list from API");
                    }

                    // Update the order list
                    orderList.clear();
                    if (orders != null && !orders.isEmpty()) {
                        orderList.addAll(orders);
                        orderAdapter.notifyDataSetChanged();
                        showEmptyView(false);
                        Log.d(TAG, "Added " + orders.size() + " orders to adapter");
                    } else {
                        showEmptyView("Bạn chưa có đơn hàng nào");
                        Log.d(TAG, "No orders found for user. Showing empty view");
                    }
                } else {
                    int errorCode = response.code();
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }

                    String errorMessage = "Không thể tải đơn hàng. Mã lỗi: " + errorCode;
                    Log.e(TAG, "API call failed. Response code: " + errorCode);
                    Log.e(TAG, "Error body: " + errorBody);

                    showEmptyView("Không thể tải đơn hàng");
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }

                showLoading(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);

                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "API call failed with exception", t);
                showEmptyView("Không thể kết nối đến máy chủ");
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null && recyclerView != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (!isLoading) {
                recyclerView.setVisibility(tvEmptyOrders.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void showEmptyView(boolean show) {
        if (tvEmptyOrders != null && recyclerView != null) {
            tvEmptyOrders.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyView(String message) {
        if (tvEmptyOrders != null) {
            tvEmptyOrders.setText(message);
            showEmptyView(true);
        }
    }
}