package com.example.testgiuaky2;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testgiuaky2.adapter.ReviewAdapter;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.config.PrefManager;
import com.example.testgiuaky2.model.CartItem;
import com.example.testgiuaky2.model.CartResponse;
import com.example.testgiuaky2.model.Category;
import com.example.testgiuaky2.model.Product;
import com.example.testgiuaky2.model.ProductDetailResponse;
import com.example.testgiuaky2.model.Review;
import com.example.testgiuaky2.model.ReviewListResponse;
import com.example.testgiuaky2.model.ReviewRequest;
import com.example.testgiuaky2.model.ReviewResponse;
import com.example.testgiuaky2.model.SingleCategoryResponse;
import com.example.testgiuaky2.service.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment implements ReviewAdapter.OnReviewActionListener {

    private static final String TAG = "ProductDetailFragment";
    private static final String ARG_PRODUCT_ID = "product_id";

    private ImageView imgProductDetail;
    private TextView tvProductDetailName, tvProductDetailPrice, tvProductDetailQuantity,
            tvProductDetailCategory, tvProductDetailDescription;
    private ProgressBar progressBarDetail;
    private FloatingActionButton btnBack, btnCart;
    private EditText edtCartQuantity;
    private Button btnDecrease, btnIncrease, btnAddToCart, btnAddReview;
    private TextView tvAverageRating, tvTotalReviews, tvNoReviews;
    private RatingBar ratingBarAverage;
    private RecyclerView rvReviews;
    private ApiService apiService;
    private int productId;
    private Product currentProduct;
    private PrefManager prefManager;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private Dialog reviewDialog;

    public static ProductDetailFragment newInstance(int productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getInt(ARG_PRODUCT_ID);
        }
        prefManager = new PrefManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        imgProductDetail = view.findViewById(R.id.imgProductDetail);
        tvProductDetailName = view.findViewById(R.id.tvProductDetailName);
        tvProductDetailPrice = view.findViewById(R.id.tvProductDetailPrice);
        tvProductDetailQuantity = view.findViewById(R.id.tvProductDetailQuantity);
        tvProductDetailCategory = view.findViewById(R.id.tvProductDetailCategory);
        tvProductDetailDescription = view.findViewById(R.id.tvProductDetailDescription);
        progressBarDetail = view.findViewById(R.id.progressBarDetail);
        btnBack = view.findViewById(R.id.btnBack);
        btnCart = view.findViewById(R.id.btnCart);

        // Initialize cart functionality views
        edtCartQuantity = view.findViewById(R.id.edtCartQuantity);
        btnDecrease = view.findViewById(R.id.btnDecrease);
        btnIncrease = view.findViewById(R.id.btnIncrease);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);

        // Initialize review functionality views
        btnAddReview = view.findViewById(R.id.btnAddReview);
        tvAverageRating = view.findViewById(R.id.tvAverageRating);
        tvTotalReviews = view.findViewById(R.id.tvTotalReviews);
        tvNoReviews = view.findViewById(R.id.tvNoReviews);
        ratingBarAverage = view.findViewById(R.id.ratingBarAverage);
        rvReviews = view.findViewById(R.id.rvReviews);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize review adapter
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(requireContext(), prefManager.getUserId(), this);
        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReviews.setAdapter(reviewAdapter);

        // Set click listeners
        setupListeners();

        // Load product details
        loadProductDetails(productId);

        // Load reviews
        loadReviews(productId);
    }

    private void setupListeners() {
        // Set back button click listener
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Set cart button click listener
        btnCart.setOnClickListener(v -> {
            // Navigate to cart fragment
            CartFragment cartFragment = new CartFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, cartFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Quantity controls
        btnDecrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(edtCartQuantity.getText().toString());
            if (quantity > 1) {
                edtCartQuantity.setText(String.valueOf(quantity - 1));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(edtCartQuantity.getText().toString());
            int availableQuantity = currentProduct != null ? currentProduct.getQuantity() : 0;
            if (quantity < availableQuantity) {
                edtCartQuantity.setText(String.valueOf(quantity + 1));
            } else {
                Toast.makeText(requireContext(), "Không thể thêm quá số lượng tồn kho", Toast.LENGTH_SHORT).show();
            }
        });

        // Add to cart button
        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null && prefManager.isLoggedIn()) {
                addToCart();
            } else if (!prefManager.isLoggedIn()) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });

        // Add review button
        btnAddReview.setOnClickListener(v -> {
            if (prefManager.isLoggedIn()) {
                showAddReviewDialog(false, null);
            } else {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập để thêm đánh giá", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails(int productId) {
        showProgress(true);
        apiService.getProductById(productId).enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ProductDetailResponse productResponse = response.body();
                    if (productResponse.isSuccess() && productResponse.getData() != null) {
                        displayProductDetails(productResponse.getData());
                    } else {
                        Log.e(TAG, "Error: " + productResponse.getMessage());
                        Toast.makeText(requireContext(), "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Error: " + response.message());
                    Toast.makeText(requireContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to fetch product details: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductDetails(Product product) {
        if (product == null) return;

        currentProduct = product;

        // Set product name
        tvProductDetailName.setText(product.getName());

        // Format and set price
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(product.getPrice()) + " VND";
        tvProductDetailPrice.setText(formattedPrice);

        // Set quantity
        tvProductDetailQuantity.setText(String.valueOf("Số lượng:" + product.getQuantity()));

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imgProductDetail);
        }

        // Load category information
        loadCategoryInfo(product.getCategoryId());

        // Set description
        tvProductDetailDescription.setText(product.getDescription());
    }

    private void loadCategoryInfo(int categoryId) {
        apiService.getCategoryById(categoryId).enqueue(new Callback<SingleCategoryResponse>() {
            @Override
            public void onResponse(Call<SingleCategoryResponse> call, Response<SingleCategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SingleCategoryResponse categoryResponse = response.body();
                    if (categoryResponse.isSuccess() && categoryResponse.getData() != null) {
                        Category category = categoryResponse.getData();
                        tvProductDetailCategory.setText("Thể loại: "+category.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleCategoryResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch category: " + t.getMessage());
            }
        });
    }

    private void addToCart() {
        try {
            int quantity = Integer.parseInt(edtCartQuantity.getText().toString().trim());
            if (quantity <= 0) {
                Toast.makeText(requireContext(), "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity > currentProduct.getQuantity()) {
                Toast.makeText(requireContext(), "Số lượng vượt quá hàng tồn kho", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create cart item
            CartItem cartItem = new CartItem(currentProduct.getId(), quantity);

            // Get user ID from PrefManager
            String userId = prefManager.getUserId();

            // Show progress
            showProgress(true);

            // Call API to add item to cart
            apiService.addItemToCart(userId, cartItem).enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    showProgress(false);
                    if (response.isSuccessful() && response.body() != null) {
                        CartResponse cartResponse = response.body();
                        if (cartResponse.isSuccess()) {
                            Toast.makeText(requireContext(), "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            // Reset quantity to 1
                            edtCartQuantity.setText("1");
                        } else {
                            Toast.makeText(requireContext(), "Lỗi: " + cartResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Lỗi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    showProgress(false);
                    Log.e(TAG, "Failed to add to cart: " + t.getMessage());
                    Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Vui lòng nhập số lượng hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    // Reviews functionality methods

    private void loadReviews(int productId) {
        showProgress(true);
        apiService.getReviewsByProduct(productId).enqueue(new Callback<ReviewListResponse>() {
            @Override
            public void onResponse(Call<ReviewListResponse> call, Response<ReviewListResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ReviewListResponse reviewResponse = response.body();
                    if (reviewResponse.isSuccess() && reviewResponse.getData() != null) {
                        Review[] reviews = reviewResponse.getData();
                        reviewList = new ArrayList<>(Arrays.asList(reviews));
                        updateReviewsUI(reviewList);
                    } else {
                        Log.e(TAG, "Error loading reviews: " + reviewResponse.getMessage());
                        showNoReviews();
                    }
                } else {
                    Log.e(TAG, "Error: " + response.message());
                    showNoReviews();
                }
            }

            @Override
            public void onFailure(Call<ReviewListResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to fetch reviews: " + t.getMessage());
                showNoReviews();
            }
        });
    }

    private void updateReviewsUI(List<Review> reviews) {
        if (reviews.isEmpty()) {
            showNoReviews();
            return;
        }

        tvNoReviews.setVisibility(View.GONE);
        rvReviews.setVisibility(View.VISIBLE);

        // Calculate average rating
        float totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        float averageRating = reviews.isEmpty() ? 0 : totalRating / reviews.size();

        // Update UI
        tvAverageRating.setText(String.format(Locale.getDefault(), "%.1f", averageRating));
        ratingBarAverage.setRating(averageRating);
        tvTotalReviews.setText(String.format(Locale.getDefault(), "%d đánh giá", reviews.size()));

        // Update adapter
        reviewAdapter.setReviews(reviews);
    }

    private void showNoReviews() {
        tvNoReviews.setVisibility(View.VISIBLE);
        rvReviews.setVisibility(View.GONE);
        tvAverageRating.setText("0.0");
        ratingBarAverage.setRating(0);
        tvTotalReviews.setText("0 đánh giá");
    }

    private void showAddReviewDialog(boolean isEdit, Review reviewToEdit) {
        reviewDialog = new Dialog(requireContext());
        reviewDialog.setContentView(R.layout.diaglog_review);
        reviewDialog.setCancelable(true);

        // Initialize dialog views
        TextView tvDialogTitle = reviewDialog.findViewById(R.id.tvDialogTitle);
        RatingBar ratingBar = reviewDialog.findViewById(R.id.ratingBarDialog);
        TextView tvRatingValue = reviewDialog.findViewById(R.id.tvRatingValue);
        TextInputEditText edtReviewContent = reviewDialog.findViewById(R.id.edtReviewContent);
        Button btnCancel = reviewDialog.findViewById(R.id.btnCancelReview);
        Button btnSubmit = reviewDialog.findViewById(R.id.btnSubmitReview);

        // Set title based on edit mode
        tvDialogTitle.setText(isEdit ? "Chỉnh sửa đánh giá" : "Thêm đánh giá");

        // Fill data if in edit mode
        if (isEdit && reviewToEdit != null) {
            ratingBar.setRating(reviewToEdit.getRating());
            tvRatingValue.setText(String.format(Locale.getDefault(), "Đánh giá: %d/5", reviewToEdit.getRating()));
            edtReviewContent.setText(reviewToEdit.getContent());
        } else {
            tvRatingValue.setText("Đánh giá: 0/5");
        }

        // Set rating change listener
        ratingBar.setOnRatingBarChangeListener((rBar, rating, fromUser) -> {
            tvRatingValue.setText(String.format(Locale.getDefault(), "Đánh giá: %d/5", (int) rating));
        });

        // Set click listeners for buttons
        btnCancel.setOnClickListener(v -> reviewDialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String content = edtReviewContent.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(requireContext(), "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit && reviewToEdit != null) {
                updateReview(reviewToEdit.getId(), content, rating);
            } else {
                submitReview(content, rating);
            }

            reviewDialog.dismiss();
        });

        reviewDialog.show();
    }

    private void submitReview(String content, int rating) {
        String userId = prefManager.getUserId();
        ReviewRequest reviewRequest = new ReviewRequest(content, rating, productId, userId);

        showProgress(true);
        apiService.createReview(reviewRequest).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse reviewResponse = response.body();
                    if (reviewResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Đánh giá đã được thêm thành công", Toast.LENGTH_SHORT).show();
                        loadReviews(productId); // Reload reviews
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + reviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi thêm đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to submit review: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReview(int reviewId, String content, int rating) {
        String userId = prefManager.getUserId();
        ReviewRequest reviewRequest = new ReviewRequest(content, rating, productId, userId);

        showProgress(true);
        apiService.updateReview(reviewId, reviewRequest).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse reviewResponse = response.body();
                    if (reviewResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Đánh giá đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                        loadReviews(productId); // Reload reviews
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + reviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi cập nhật đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to update review: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteReview(int reviewId) {
        showProgress(true);
        apiService.deleteReview(reviewId).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                showProgress(false);
                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse reviewResponse = response.body();
                    if (reviewResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Đánh giá đã được xóa thành công", Toast.LENGTH_SHORT).show();
                        loadReviews(productId); // Reload reviews
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + reviewResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi xóa đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                showProgress(false);
                Log.e(TAG, "Failed to delete review: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress(boolean show) {
        if (progressBarDetail != null) {
            progressBarDetail.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // Implementation of ReviewAdapter.OnReviewActionListener interface

    @Override
    public void onEditReview(Review review) {
        showAddReviewDialog(true, review);
    }

    @Override
    public void onDeleteReview(Review review) {
        // Show confirmation dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa đánh giá này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteReview(review.getId());
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}