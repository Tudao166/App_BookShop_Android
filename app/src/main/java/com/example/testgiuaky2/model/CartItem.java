package com.example.testgiuaky2.model;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    private String id; // ID mục giỏ hàng từ API
    private int productId;
    private int quantity;
    private Product product;

    // Các trường bổ sung từ API
    @SerializedName("productName")
    private String productName;

    @SerializedName("productImageUrl")
    private String productImageUrl;

    @SerializedName("productPrice")
    private double productPrice;

    @SerializedName("subtotal")
    private double subtotal;

    // Constructor rỗng cho Gson
    public CartItem() {
    }

    // Constructor cho việc thêm/cập nhật sản phẩm
    public CartItem(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Constructor đầy đủ
    public CartItem(int productId, int quantity, Product product) {
        this.productId = productId;
        this.quantity = quantity;
        this.product = product;
    }

    // Getter và Setter
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        // Nếu product là null nhưng có thông tin sản phẩm riêng lẻ, tạo đối tượng product mới
        if (product == null && productName != null) {
            product = new Product();
            product.setId(productId);
            product.setName(productName);
            product.setImageUrl(productImageUrl);
            product.setPrice(productPrice);
            product.setQuantity(100); // Giá trị mặc định
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Getter và Setter cho các trường bổ sung
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}