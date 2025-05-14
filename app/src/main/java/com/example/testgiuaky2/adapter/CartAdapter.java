package com.example.testgiuaky2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testgiuaky2.R;
import com.example.testgiuaky2.model.CartItem;
import com.example.testgiuaky2.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final Context context;
    private final OnCartItemActionListener listener;

    // Interface for cart item actions
    public interface OnCartItemActionListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
        void onInvalidItemDetected(CartItem item); // New callback for invalid items
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemActionListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        if (product == null) {
            // Handle null product
            holder.tvProductName.setText("Sản phẩm không có thông tin");
            holder.tvProductPrice.setText("0 VND");
            holder.tvSubtotal.setText("0 VND");
            holder.imgProduct.setImageResource(R.drawable.error_image);

            // Disable quantity controls
            holder.btnDecrease.setEnabled(false);
            holder.btnIncrease.setEnabled(false);
            holder.edtQuantity.setEnabled(false);

            // Set quantity to show actual value in cart
            holder.edtQuantity.setText(String.valueOf(cartItem.getQuantity()));

            // Notify fragment about invalid item for removal
            if (listener != null) {
                holder.btnRemove.setOnClickListener(v -> listener.onRemoveItem(cartItem));

                // Optional: Notify fragment about invalid item for potential auto-removal
                listener.onInvalidItemDetected(cartItem);
            }

            return;
        }

        // Set product data
        holder.tvProductName.setText(product.getName());

        // Format and set price
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(product.getPrice()) + " VND";
        holder.tvProductPrice.setText(formattedPrice);

        // Set quantity
        holder.edtQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Calculate and set subtotal
        double subtotal = cartItem.getQuantity() * product.getPrice();
        String formattedSubtotal = formatter.format(subtotal) + " VND";
        holder.tvSubtotal.setText(formattedSubtotal);

        // Load product image
        String imageUrl = product.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_image);
        }

        // Set quantity controls
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.edtQuantity.getText().toString());
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                holder.edtQuantity.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged(cartItem, newQuantity);
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.edtQuantity.getText().toString());
            int availableQuantity = product.getQuantity();

            if (currentQuantity < availableQuantity) {
                int newQuantity = currentQuantity + 1;
                holder.edtQuantity.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged(cartItem, newQuantity);
            }
        });

        // Set remove button
        holder.btnRemove.setOnClickListener(v -> {
            listener.onRemoveItem(cartItem);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newCartItems);
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvSubtotal;
        Button btnDecrease, btnIncrease;
        ImageButton btnRemove;
        EditText edtQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgCartProduct);
            tvProductName = itemView.findViewById(R.id.tvCartProductName);
            tvProductPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            btnDecrease = itemView.findViewById(R.id.btnCartDecrease);
            btnIncrease = itemView.findViewById(R.id.btnCartIncrease);
            btnRemove = itemView.findViewById(R.id.btnRemoveItem);
            edtQuantity = itemView.findViewById(R.id.edtCartQuantity);
        }
    }
}
