package com.example.testgiuaky2.service;
import com.example.testgiuaky2.model.ApiResponseModels.ProductResponse;
import java.util.List;

import com.example.testgiuaky2.model.Account;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.CartItem;
import com.example.testgiuaky2.model.CartResponse;
import com.example.testgiuaky2.model.CategoryResponse;
import com.example.testgiuaky2.model.Account;
import com.example.testgiuaky2.model.ApiResponse;
import com.example.testgiuaky2.model.ForgotPasswordRequest;
import com.example.testgiuaky2.model.Order;
import com.example.testgiuaky2.model.OrderRequest;
import com.example.testgiuaky2.model.OrderStatusRequest;
import com.example.testgiuaky2.model.OtpRequest;
import com.example.testgiuaky2.model.Category;
import com.example.testgiuaky2.model.Product;
import com.example.testgiuaky2.model.ProductDetailResponse;
import com.example.testgiuaky2.model.ResetPasswordRequest;
import com.example.testgiuaky2.model.ReviewListResponse;
import com.example.testgiuaky2.model.ReviewRequest;
import com.example.testgiuaky2.model.ReviewResponse;
import com.example.testgiuaky2.model.SingleCategoryResponse;
import com.example.testgiuaky2.model.User;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
/*Trịnh Trung Hào - 22110316*/
public interface ApiService {
    @GET("api/v1/users/{id}")
    Call<ApiResponse<User>> getUser(@Path("id") String userId);

    @PUT("api/v1/users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") String userId, @Body User user);

    @POST("api/v1/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/v1/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);

    @GET("api/v1/products")
    Call<ProductResponse> getProducts();
    @POST("api/v1/login")
    Call<Account> login(@Path("username") String username, @Path("password") String password);

    @POST("api/v1/login")
    Call<ApiResponse<User>> login(@Body Account account);
    @POST("api/v1/verify-user")
    Call<ApiResponse> verifyOtp(@Body OtpRequest otpRequest);

    @POST("api/v1/register")
    Call<ApiResponse<User>> register(@Body User user);

    @GET("api/v1/products/{id}")
    Call<ProductDetailResponse> getProductById(@Path("id") int id);

    @GET("api/v1/products/category/{categoryId}")
    Call<ProductResponse> getProductsByCategory(@Path("categoryId") int categoryId);

    @GET("api/v1/products/search")
    Call<ProductResponse> searchProducts(@Query("name") String name);

    @POST("api/v1/products")
    Call<ProductResponse> createProduct(@Body Product product);

    @PUT("api/v1/products/{id}")
    Call<ProductResponse> updateProduct(@Path("id") int id, @Body Product product);

    @DELETE("api/v1/products/{id}")
    Call<ProductResponse> deleteProduct(@Path("id") int id);

    // Category Endpoints
    @GET("api/v1/categories")
    Call<CategoryResponse> getCategories();

    @GET("api/v1/categories/{id}")
    Call<SingleCategoryResponse> getCategoryById(@Path("id") int id);

    @POST("api/v1/categories")
    Call<CategoryResponse> createCategory(@Body Category category);

    @PUT("api/v1/categories/{id}")
    Call<CategoryResponse> updateCategory(@Path("id") int id, @Body Category category);

    @DELETE("api/v1/categories/{id}")
    Call<CategoryResponse> deleteCategory(@Path("id") int id);


    //Cart
    // Add these methods to your ApiService interface
    @GET("api/v1/carts/{userId}")
    Call<CartResponse> getCart(@Path("userId") String userId);

    @POST("api/v1/carts/{userId}/items")
    Call<CartResponse> addItemToCart(@Path("userId") String userId, @Body CartItem item);

    @PUT("api/v1/carts/{userId}/items")
    Call<CartResponse> updateCartItem(@Path("userId") String userId, @Body CartItem item);

    @DELETE("api/v1/carts/{userId}/items/{productId}")
    Call<CartResponse> removeItemFromCart(@Path("userId") String userId, @Path("productId") int productId);

    @DELETE("api/v1/carts/{userId}")
    Call<CartResponse> clearCart(@Path("userId") String userId);


    // Review endpoints
    @GET("api/v1/reviews")
    Call<ReviewListResponse> getAllReviews();

    @GET("api/v1/reviews/{id}")
    Call<ReviewResponse> getReviewById(@Path("id") int id);

    @GET("api/v1/reviews/product/{productId}")
    Call<ReviewListResponse> getReviewsByProduct(@Path("productId") int productId);

    @GET("api/v1/reviews/user/{userId}")
    Call<ReviewListResponse> getReviewsByUser(@Path("userId") String userId);

    @POST("api/v1/reviews")
    Call<ReviewResponse> createReview(@Body ReviewRequest review);

    @PUT("api/v1/reviews/{id}")
    Call<ReviewResponse> updateReview(@Path("id") int id, @Body ReviewRequest review);

    @DELETE("api/v1/reviews/{id}")
    Call<ReviewResponse> deleteReview(@Path("id") int id);


    // Order endpoints
    @POST("api/v1/orders/checkout/{userId}")
    Call<ApiResponse<Order>> checkoutOrder(@Path("userId") String userId, @Body OrderRequest orderRequest);

    @GET("api/v1/orders/{orderId}")
    Call<ApiResponse<Order>> getOrderById(@Path("orderId") String orderId);

    @GET("api/v1/orders/user/{userId}")
    Call<ApiResponse<List<Order>>> getUserOrders(@Path("userId") String userId);

    @PUT("api/v1/orders/{orderId}/status")
    Call<ApiResponse<Order>> updateOrderStatus(@Path("orderId") String orderId, @Body OrderStatusRequest statusRequest);

    @PUT("api/v1/orders/{orderId}/cancel")
    Call<ApiResponse<Order>> cancelOrder(@Path("orderId") String orderId);


}
