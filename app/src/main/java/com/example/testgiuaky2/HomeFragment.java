package com.example.testgiuaky2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.testgiuaky2.config.PrefManager;
import com.example.testgiuaky2.model.ApiResponseModels.ProductResponse;
import com.example.testgiuaky2.model.Product;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private GridView gridView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ApiService apiService;
    private View rootView;
    private TextView tvUserName;
    private ImageView avtUser;
    private PrefManager prefManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize PrefManager
        prefManager = new PrefManager(getContext());

        // Initialize views
        tvUserName = rootView.findViewById(R.id.tvUserName);
        avtUser = rootView.findViewById(R.id.avtUser);
        gridView = rootView.findViewById(R.id.gridview1);
        SearchView searchView = rootView.findViewById(R.id.searchView);

        // Set user information
        updateUserInfo();

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize product list and adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        gridView.setAdapter(productAdapter);

        // Set up search functionality
        setupSearchView(searchView);

        // Load products from API
        loadProducts();

        // Set item click listener
        setupItemClickListener();
    }

    private void updateUserInfo() {
        // Check if user is logged in
        if (prefManager.isLoggedIn()) {
            // Update username
            String fullName = prefManager.getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                tvUserName.setText("Hi! " + fullName);
            }

            // Update profile picture
            String pictureUrl = prefManager.getPicture();
            if (pictureUrl != null && !pictureUrl.isEmpty()) {
                // Use Glide to load the image
                Glide.with(this)
                        .load(pictureUrl)
                        .placeholder(R.drawable.pro_trung) // Use the default image as placeholder
                        .error(R.drawable.pro_trung) // Show default image if loading fails
                        .into(avtUser);
            }
        }
    }

    private void setupSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return false;
            }
        });
    }

    private void filterProducts(String query) {
        if (productList == null) return;

        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }

        productAdapter.updateData(filteredList);
    }

    private void loadProducts() {
        Call<ProductResponse> call = apiService.getProducts();
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    if (productResponse.isSuccess() && productResponse.getData() != null) {
                        productList = productResponse.getData();
                        productAdapter.updateData(productList);
                        Log.d(TAG, "Products loaded: " + productList.size());
                    } else {
                        showError("Error: " + productResponse.getMessage());
                    }
                } else {
                    showError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void setupItemClickListener() {
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = (Product) productAdapter.getItem(position);
            Toast.makeText(getContext(),
                    "Selected: " + selectedProduct.getName(),
                    Toast.LENGTH_SHORT).show();

            // Here you could start a new activity to show product details
            // Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            // intent.putExtra("PRODUCT_ID", selectedProduct.getId());
            // startActivity(intent);

            // Or you could replace this fragment with a detail fragment
            // ProductDetailFragment detailFragment = new ProductDetailFragment();
            // Bundle args = new Bundle();
            // args.putInt("PRODUCT_ID", selectedProduct.getId());
            // detailFragment.setArguments(args);
            // getParentFragmentManager().beginTransaction()
            //     .replace(R.id.fragment_container, detailFragment)
            //     .addToBackStack(null)
            //     .commit();
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    // Add onResume to refresh user data when coming back to this fragment
    @Override
    public void onResume() {
        super.onResume();
        // Update user info in case it has changed
        updateUserInfo();
    }
}