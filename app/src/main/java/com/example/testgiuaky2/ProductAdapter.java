package com.example.testgiuaky2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.testgiuaky2.R;
import com.example.testgiuaky2.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private LayoutInflater inflater;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return productList != null ? productList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.product_item, parent, false);
            holder = new ViewHolder();
            holder.imgProduct = convertView.findViewById(R.id.imgProduct);
            holder.tvProductName = convertView.findViewById(R.id.tvProductName);
            holder.tvProductPrice = convertView.findViewById(R.id.tvProductPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);

        // Set product name
        holder.tvProductName.setText(product.getName());

        // Format and set price
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(product.getPrice()) + " VND";
        holder.tvProductPrice.setText(formattedPrice);

        // Load image using Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.imgProduct);

        return convertView;
    }

    public void updateData(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvProductPrice;
    }
}