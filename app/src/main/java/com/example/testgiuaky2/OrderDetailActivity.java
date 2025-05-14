package com.example.testgiuaky2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testgiuaky2.adapter.OrderItemAdapter;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.Order;
import com.example.testgiuaky2.service.ApiService;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetailActivity";

    private TextView tvOrderId;
    private TextView tvOrderDate;
    private TextView tvOrderStatus;
    private TextView tvShippingAddress;
    private TextView tvPhoneNumber;
    private TextView tvDeliveryDate;
    private TextView tvTotalAmount;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;
    private Button btnCancelOrder;
    private ImageView btnBack;

    private ApiService apiService;
    private String orderId;
    private Order currentOrder;
    private OrderItemAdapter orderItemAdapter;
    private SimpleDateFormat dateFormat;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Initialize views
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        recyclerViewItems = findViewById(R.id.recyclerViewOrderItems);
        progressBar = findViewById(R.id.progressBar);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnBack = findViewById(R.id.btnBack);

        // Format utilities
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Get order ID from intent
        orderId = getIntent().getStringExtra("ORDER_ID");
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup recycler view
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        // Load order details
        loadOrderDetails();

        // Setup back button
        btnBack.setOnClickListener(v -> finish());

        // Setup cancel order button
        btnCancelOrder.setOnClickListener(v -> cancelOrder());
    }

    private void loadOrderDetails() {
        showLoading(true);
        Log.d(TAG, "Loading order details for order ID: " + orderId);

        apiService.getOrderById(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API call successful. Response code: " + response.code());
                    ApiResponse<Order> apiResponse = response.body();

                    // Check for data field first
                    Order order = apiResponse.getData();

                    // Fallback to getResult() if getData() returns null
                    if (order == null) {
                        order = apiResponse.getResult();
                        Log.d(TAG, "Using getResult() because getData() returned null");
                    }

                    if (order != null) {
                        Log.d(TAG, "Order details loaded successfully for order: " + order.getId());
                        currentOrder = order;
                        populateOrderDetails();
                    } else {
                        Log.e(TAG, "Order object is null in the response");
                        Toast.makeText(OrderDetailActivity.this, "Không thể tải thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                        finish();
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

                    String errorMessage = "Không thể tải thông tin đơn hàng. Mã lỗi: " + errorCode;
                    Log.e(TAG, "API call failed. Response code: " + errorCode);
                    Log.e(TAG, "Error body: " + errorBody);

                    Toast.makeText(OrderDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    finish();
                }

                showLoading(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                showLoading(false);

                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "API call failed with exception", t);
                Toast.makeText(OrderDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateOrderDetails() {
        if (currentOrder == null) return;

        // Set order ID
        tvOrderId.setText(String.format("Đơn hàng #%s", currentOrder.getId().substring(0, 8)));

        // Set order date
        if (currentOrder.getOrderDate() != null) {
            tvOrderDate.setText(dateFormat.format(currentOrder.getOrderDate()));
        } else {
            tvOrderDate.setText("N/A");
        }

        // Set order status
        tvOrderStatus.setText(currentOrder.getStatusDisplay());

        // Set shipping address
        tvShippingAddress.setText(currentOrder.getShippingAddress());

        // Set phone number
        tvPhoneNumber.setText(currentOrder.getPhoneNumber());

        // Set delivery date
        if (currentOrder.getDeliveryDate() != null) {
            tvDeliveryDate.setText(dateFormat.format(currentOrder.getDeliveryDate()));
            tvDeliveryDate.setVisibility(View.VISIBLE);
        } else {
            tvDeliveryDate.setVisibility(View.GONE);
        }

        // Set total amount
        tvTotalAmount.setText(currencyFormat.format(currentOrder.getTotalAmount()));

        // Set order items
        orderItemAdapter = new OrderItemAdapter(currentOrder.getItems(), this);
        recyclerViewItems.setAdapter(orderItemAdapter);

        // Show/hide cancel button based on order status
        btnCancelOrder.setVisibility("PENDING".equals(currentOrder.getStatus()) ? View.VISIBLE : View.GONE);
    }

    private void cancelOrder() {
        if (currentOrder == null) return;

        showLoading(true);
        Log.d(TAG, "Cancelling order: " + orderId);

        apiService.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Order cancelled successfully");
                    ApiResponse<Order> apiResponse = response.body();

                    // Check for data field first
                    Order order = apiResponse.getData();

                    // Fallback to getResult() if getData() returns null
                    if (order == null) {
                        order = apiResponse.getResult();
                        Log.d(TAG, "Using getResult() because getData() returned null");
                    }

                    if (order != null) {
                        currentOrder = order;
                        Toast.makeText(OrderDetailActivity.this, "Đơn hàng đã được hủy thành công", Toast.LENGTH_SHORT).show();
                        populateOrderDetails();
                    } else {
                        Log.e(TAG, "Order object is null in the cancel response");
                        Toast.makeText(OrderDetailActivity.this, "Đã xảy ra lỗi khi hủy đơn hàng", Toast.LENGTH_SHORT).show();
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

                    String errorMessage = "Không thể hủy đơn hàng. Mã lỗi: " + errorCode;
                    Log.e(TAG, "API call failed. Response code: " + errorCode);
                    Log.e(TAG, "Error body: " + errorBody);

                    Toast.makeText(OrderDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                showLoading(false);
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                showLoading(false);

                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "API call failed with exception", t);
                Toast.makeText(OrderDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}