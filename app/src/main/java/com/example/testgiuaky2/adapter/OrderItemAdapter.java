package com.example.testgiuaky2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testgiuaky2.R;
import com.example.testgiuaky2.model.OrderItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private final List<OrderItem> orderItems;
    private final Context context;
    private final NumberFormat currencyFormat;

    public OrderItemAdapter(List<OrderItem> orderItems, Context context) {
        this.orderItems = orderItems;
        this.context = context;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);

        // Load product image
        if (item.getProductImageUrl() != null && !item.getProductImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getProductImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_image);
        }

        // Set product name
        holder.tvProductName.setText(item.getProductName());

        // Set product price
        holder.tvProductPrice.setText(currencyFormat.format(item.getPrice()));

        // Set product quantity
        holder.tvProductQuantity.setText(String.format("x%d", item.getQuantity()));

        // Set subtotal
        holder.tvSubtotal.setText(currencyFormat.format(item.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductQuantity;
        TextView tvSubtotal;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}