package com.example.testgiuaky2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testgiuaky2.OrderDetailActivity;
import com.example.testgiuaky2.R;
import com.example.testgiuaky2.OrderDetailActivity;
import com.example.testgiuaky2.model.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<Order> orderList;
    private final Context context;
    private final SimpleDateFormat dateFormat;
    private final NumberFormat currencyFormat;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set order ID
        holder.tvOrderId.setText(String.format("Đơn hàng #%s", order.getId().substring(0, 8)));

        // Set order date
        if (order.getOrderDate() != null) {
            holder.tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
        } else {
            holder.tvOrderDate.setText("N/A");
        }

        // Set order status
        holder.tvOrderStatus.setText(order.getStatusDisplay());

        // Set order total
        holder.tvOrderTotal.setText(currencyFormat.format(order.getTotalAmount()));

        // Set shipping address (truncated)
        String address = order.getShippingAddress();
        if (address != null && address.length() > 30) {
            address = address.substring(0, 27) + "...";
        }
        holder.tvShippingAddress.setText(address);

        // Set item count
        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        holder.tvItemCount.setText(String.format("%d sản phẩm", itemCount));

        // Set click listener to open order details
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvOrderId;
        TextView tvOrderDate;
        TextView tvOrderStatus;
        TextView tvOrderTotal;
        TextView tvShippingAddress;
        TextView tvItemCount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewOrder);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvShippingAddress = itemView.findViewById(R.id.tvShippingAddress);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
        }
    }
}