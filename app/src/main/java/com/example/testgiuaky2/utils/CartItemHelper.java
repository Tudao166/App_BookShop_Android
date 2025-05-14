package com.example.testgiuaky2.utils;

import android.util.Log;
import com.example.testgiuaky2.model.CartItem;
import com.example.testgiuaky2.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartItemHelper {
    private static final String TAG = "CartItemHelper";

    /**
     * Đảm bảo CartItem có đối tượng Product đầy đủ.
     * Nếu Product là null nhưng có thông tin sản phẩm trong CartItem, tạo đối tượng Product từ dữ liệu có sẵn.
     *
     * @param item CartItem cần kiểm tra và sửa chữa
     * @return CartItem đã được sửa đổi với đối tượng Product không null (nếu có thể)
     */
    public static CartItem ensureProductInfo(CartItem item) {
        if (item == null) {
            return null;
        }

        // Kiểm tra nếu product là null, nhưng có thể tạo từ dữ liệu có sẵn
        if (item.getProduct() == null) {
            try {
                // Chỉ tạo Product mới nếu có đủ thông tin cần thiết
                String productName = item.getProductName();
                String imageUrl = item.getProductImageUrl();
                double price = item.getProductPrice();

                if (productName != null) {
                    // Tạo đối tượng Product mới từ dữ liệu có sẵn
                    Product product = new Product();
                    product.setId(item.getProductId());
                    product.setName(productName);

                    // Thiết lập những thông tin có thể null an toàn
                    if (imageUrl != null) {
                        product.setImageUrl(imageUrl);
                    }

                    product.setPrice(price);
                    // Đặt số lượng mặc định
                    product.setQuantity(100);

                    // Cập nhật CartItem với product mới
                    item.setProduct(product);
                    Log.d(TAG, "Created Product object for item: " + item.getProductId());
                }
            } catch (Exception e) {
                // Nếu có lỗi, ghi log và giữ nguyên item
                Log.e(TAG, "Không thể tạo thông tin sản phẩm từ dữ liệu CartItem: " + e.getMessage());
            }
        }

        return item;
    }

    /**
     * Xử lý danh sách CartItem để đảm bảo tất cả các mục đều có thông tin sản phẩm đầy đủ
     *
     * @param items Danh sách CartItem cần xử lý
     * @return Danh sách CartItem đã được xử lý
     */
    public static List<CartItem> processCartItems(List<CartItem> items) {
        if (items == null) {
            return new ArrayList<>();
        }

        List<CartItem> processedItems = new ArrayList<>();
        for (CartItem item : items) {
            CartItem processedItem = ensureProductInfo(item);
            if (processedItem != null) {
                processedItems.add(processedItem);
            }
        }

        return processedItems;
    }
}