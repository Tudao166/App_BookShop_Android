package com.example.testgiuaky2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testgiuaky2.adapter.CartAdapter;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.config.PrefManager;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.Cart;
import com.example.testgiuaky2.model.CartItem;
import com.example.testgiuaky2.model.CartResponse;
import com.example.testgiuaky2.model.Order;
import com.example.testgiuaky2.model.OrderItem;
import com.example.testgiuaky2.model.OrderRequest;
import com.example.testgiuaky2.service.ApiService;
import com.example.testgiuaky2.utils.CartItemHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemActionListener {

    private static final String TAG = "CartFragment";

    private RecyclerView recyclerViewCart;
    private TextView tvEmptyCart, tvTotalPrice;
    private ProgressBar progressBarCart;
    private FloatingActionButton btnBackFromCart;
    private Button btnClearCart, btnCheckout;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private ApiService apiService;
    private PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(requireContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        cartItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        tvEmptyCart = view.findViewById(R.id.tvEmptyCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        progressBarCart = view.findViewById(R.id.progressBarCart);
        btnBackFromCart = view.findViewById(R.id.btnBackFromCart);
        btnClearCart = view.findViewById(R.id.btnClearCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup listeners
        setupListeners();

        // Load cart data
        loadCartData();
    }

    private void setupRecyclerView() {
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartAdapter = new CartAdapter(requireContext(), cartItems, this);
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        btnBackFromCart.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        btnClearCart.setOnClickListener(v -> {
            clearCart();
        });

        btnCheckout.setOnClickListener(v -> {
            showCheckoutDialog();
        });
    }

    private void showCheckoutDialog() {
        if (!prefManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra giỏ hàng có trống không
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate layout cho dialog
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_checkout, null);

        // Lấy reference đến các view trong dialog
        EditText etShippingAddress = dialogView.findViewById(R.id.etShippingAddress);
        EditText etPhoneNumber = dialogView.findViewById(R.id.etPhoneNumber);

        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông tin đặt hàng");
        builder.setView(dialogView);

        builder.setPositiveButton("Xác nhận", null); // Sẽ override ở dưới
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Show dialog
        dialog.show();

        // Override nút positive để không tự dismiss khi validation fail
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String shippingAddress = etShippingAddress.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();

            // Validation
            if (shippingAddress.isEmpty()) {
                etShippingAddress.setError("Vui lòng nhập địa chỉ giao hàng");
                return;
            }

            if (phoneNumber.isEmpty()) {
                etPhoneNumber.setError("Vui lòng nhập số điện thoại");
                return;
            }

            // Validate phone number format (example: must be 10 digits)
            if (!phoneNumber.matches("^0\\d{9}$")) {
                etPhoneNumber.setError("Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và có 10 số)");
                return;
            }

            // Nếu validation ok, tiến hành checkout
            dialog.dismiss();
            processCheckout(shippingAddress, phoneNumber);
        });
    }

    private void processCheckout(String shippingAddress, String phoneNumber) {
        if (!prefManager.isLoggedIn()) return;

        String userId = prefManager.getUserId();
        OrderRequest orderRequest = new OrderRequest(shippingAddress, phoneNumber);

        showProgress(true);

        apiService.checkoutOrder(userId, orderRequest).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Order order = apiResponse.getData();
                        // Hiển thị thông báo thành công
                        Toast.makeText(requireContext(), "Đặt hàng thành công! Mã đơn hàng: " + order.getId(), Toast.LENGTH_LONG).show();

                        // Hiển thị dialog thông tin đơn hàng
                        showOrderConfirmationDialog(order);

                        // Reset cart UI
                        cartItems.clear();
                        cartAdapter.notifyDataSetChanged();
                        showEmptyCart(true);
                    } else {
                        // Hiển thị lỗi từ server
                        Toast.makeText(requireContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Hiển thị lỗi từ response
                    Toast.makeText(requireContext(), "Lỗi khi đặt hàng. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error response: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                showProgress(false);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Checkout failure: " + t.getMessage());
            }
        });
    }

    private void showOrderConfirmationDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông tin đơn hàng");

        StringBuilder message = new StringBuilder();
        message.append("Mã đơn hàng: ").append(order.getId()).append("\n\n");
        message.append("Địa chỉ giao hàng: ").append(order.getShippingAddress()).append("\n");
        message.append("Số điện thoại: ").append(order.getPhoneNumber()).append("\n\n");
        message.append("Sản phẩm:\n");

        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem item = order.getItems().get(i);
            message.append(i + 1).append(". ")
                    .append(item.getProductName())
                    .append(" (").append(item.getQuantity()).append(" x ")
                    .append(formatPrice(item.getPrice())).append(")\n");
        }

        message.append("\nTổng tiền: ").append(formatPrice(order.getTotalAmount()));
        message.append("\nTrạng thái: ").append(order.getStatusDisplay());

        builder.setMessage(message.toString());
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(price) + " VND";
    }

    private void loadCartData() {
        if (!prefManager.isLoggedIn()) {
            showEmptyCart(true);
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = prefManager.getUserId();
        showProgress(true);

        apiService.getCart(userId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    if (cartResponse.isSuccess() && cartResponse.getData() != null) {
                        // Xử lý dữ liệu giỏ hàng trước khi cập nhật UI
                        processAndUpdateCartUI(cartResponse.getData());
                    } else {
                        showEmptyCart(true);
                        Log.e(TAG, "Error: " + cartResponse.getMessage());
                    }
                } else {
                    showEmptyCart(true);
                    Log.e(TAG, "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showProgress(false);
                showEmptyCart(true);
                Log.e(TAG, "Failed to fetch cart: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processAndUpdateCartUI(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            showEmptyCart(true);
            return;
        }

        // Ghi log để kiểm tra dữ liệu
        for (CartItem item : cart.getItems()) {
            Log.d(TAG, "CartItem trước khi xử lý - productId: " + item.getProductId() +
                    ", productName: " + (item.getProductName() != null ? item.getProductName() : "null") +
                    ", product object: " + (item.getProduct() != null ? "not null" : "null"));
        }

        // Xử lý danh sách CartItem để đảm bảo tất cả các mục đều có thông tin sản phẩm
        List<CartItem> processedItems = CartItemHelper.processCartItems(cart.getItems());
        cart.setItems(processedItems);

        // Ghi log lại để kiểm tra sau khi xử lý
        for (CartItem item : cart.getItems()) {
            Log.d(TAG, "CartItem sau khi xử lý - productId: " + item.getProductId() +
                    ", product object: " + (item.getProduct() != null ? "not null" : "null") +
                    ", product name: " + (item.getProduct() != null ? item.getProduct().getName() : "null"));
        }

        showEmptyCart(false);
        cartItems.clear();
        cartItems.addAll(cart.getItems());
        cartAdapter.notifyDataSetChanged();

        // Update total price
        updateTotalPrice(cart.getTotalPrice());
    }

    private void updateTotalPrice(double totalPrice) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(totalPrice) + " VND";
        tvTotalPrice.setText("Tổng tiền: " + formattedPrice);
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (!prefManager.isLoggedIn()) return;

        String userId = prefManager.getUserId();
        CartItem updatedItem = new CartItem(item.getProductId(), newQuantity);

        showProgress(true);

        apiService.updateCartItem(userId, updatedItem).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    if (cartResponse.isSuccess() && cartResponse.getData() != null) {
                        processAndUpdateCartUI(cartResponse.getData());
                        Toast.makeText(requireContext(), "Đã cập nhật số lượng", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + cartResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Reload the cart to get the correct state
                        loadCartData();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
                    loadCartData();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to update cart item: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadCartData();
            }
        });
    }

    @Override
    public void onRemoveItem(CartItem item) {
        if (!prefManager.isLoggedIn()) return;

        String userId = prefManager.getUserId();

        showProgress(true);

        apiService.removeItemFromCart(userId, item.getProductId()).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    if (cartResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                        processAndUpdateCartUI(cartResponse.getData());
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + cartResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        loadCartData();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                    loadCartData();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to remove cart item: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadCartData();
            }
        });
    }

    @Override
    public void onInvalidItemDetected(CartItem item) {
        // Implementation for handling invalid items
        Toast.makeText(requireContext(), "Phát hiện sản phẩm không hợp lệ: " + item.getProductId(), Toast.LENGTH_SHORT).show();
        // You might want to remove the invalid item
        if (item.getProductId() > 0) {
            onRemoveItem(item);
        } else {
            // If there's no valid productId, just refresh the cart
            loadCartData();
        }
    }

    private void clearCart() {
        if (!prefManager.isLoggedIn()) return;

        String userId = prefManager.getUserId();

        showProgress(true);

        apiService.clearCart(userId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    if (cartResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Đã xóa toàn bộ giỏ hàng", Toast.LENGTH_SHORT).show();
                        showEmptyCart(true);
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + cartResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to clear cart: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyCart(boolean isEmpty) {
        if (isEmpty) {
            recyclerViewCart.setVisibility(View.GONE);
            tvEmptyCart.setVisibility(View.VISIBLE);
            btnClearCart.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.GONE);
            tvTotalPrice.setVisibility(View.GONE);
        } else {
            recyclerViewCart.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.GONE);
            btnClearCart.setVisibility(View.VISIBLE);
            btnCheckout.setVisibility(View.VISIBLE);
            tvTotalPrice.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress(boolean show) {
        if (progressBarCart != null) {
            progressBarCart.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}