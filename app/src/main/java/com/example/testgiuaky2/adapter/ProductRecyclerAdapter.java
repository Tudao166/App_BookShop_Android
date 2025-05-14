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
import com.example.testgiuaky2.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductRecyclerAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product name
        holder.tvProductName.setText(product.getName());

        // Format and set price
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(product.getPrice()) + " VND";
        holder.tvProductPrice.setText(formattedPrice);

        // Set quantity
        holder.tvProductQuantity.setText("Số lượng: " + product.getQuantity());

        // Load image using Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.imgProduct);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void updateData(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
        }
    }
}