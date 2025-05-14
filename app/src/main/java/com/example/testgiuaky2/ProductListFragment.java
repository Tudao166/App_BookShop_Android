package com.example.testgiuaky2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.testgiuaky2.R;
import com.example.testgiuaky2.adapter.ProductRecyclerAdapter;
import com.example.testgiuaky2.api.ApiClient;
import com.example.testgiuaky2.service.ApiService;
import com.example.testgiuaky2.model.CategoryResponse;
import com.example.testgiuaky2.model.ApiResponseModels.ProductResponse;
import com.example.testgiuaky2.model.Category;
import com.example.testgiuaky2.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment {

    private static final String TAG = "ProductListFragment";
    private RecyclerView recyclerView;
    private ProductRecyclerAdapter productAdapter;
    private List<Product> productList;
    private ApiService apiService;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private Spinner categorySpinner;
    private List<Category> categoryList;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        searchView = view.findViewById(R.id.searchView);
        categorySpinner = view.findViewById(R.id.categorySpinner);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize product list and adapter
        productList = new ArrayList<>();
        productAdapter = new ProductRecyclerAdapter(requireContext(), productList);

        // Set click listener for product items
        productAdapter.setOnProductClickListener(product -> {
            // Navigate to product detail fragment
            navigateToProductDetail(product.getId());
        });


        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(productAdapter);

        // Setup SwipeRefreshLayout
        setupSwipeRefresh();

        // Setup SearchView
        setupSearchView();

        // Load categories for spinner
        loadCategories();

        // Load all products initially
        loadProducts();
    }

    private void navigateToProductDetail(int productId) {
        ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(productId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment) // Thay đổi id container nếu cần
                .addToBackStack(null)
                .commit();
    }


    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Clear search when refreshing
            searchView.setQuery("", false);
            searchView.clearFocus();

            // Reset category selection
            if (categorySpinner.getAdapter() != null) {
                categorySpinner.setSelection(0);
            }

            // Reload all products
            loadProducts();
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If search is cleared and no category selected, load all products
                    if (categorySpinner.getSelectedItemPosition() == 0) {
                        loadProducts();
                    }
                }
                return false;
            }
        });
    }

    private void loadCategories() {
        showProgress(true);
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryResponse categoryResponse = response.body();
                    if (categoryResponse.isSuccess() && categoryResponse.getData() != null) {
                        categoryList = categoryResponse.getData();
                        setupCategorySpinner(categoryList);
                    } else {
                        showError("Error loading categories: " + categoryResponse.getMessage());
                    }
                } else {
                    showError("Error: " + response.message());
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "API call failed", t);
                showProgress(false);
            }
        });
    }

    private void setupCategorySpinner(List<Category> categories) {
        // Add "All Categories" option at the beginning
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("Tất cả danh mục");

        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Clear search query when category changes
                searchView.setQuery("", false);
                searchView.clearFocus();

                if (position == 0) {
                    // "All Categories" selected
                    loadProducts();
                } else {
                    // Specific category selected
                    int categoryId = categories.get(position - 1).getId();
                    loadProductsByCategory(categoryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadProducts() {
        showProgress(true);
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
                swipeRefreshLayout.setRefreshing(false);
                showProgress(false);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "API call failed", t);
                swipeRefreshLayout.setRefreshing(false);
                showProgress(false);
            }
        });
    }

    private void loadProductsByCategory(int categoryId) {
        showProgress(true);
        Call<ProductResponse> call = apiService.getProductsByCategory(categoryId);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    if (productResponse.isSuccess() && productResponse.getData() != null) {
                        productList = productResponse.getData();
                        productAdapter.updateData(productList);
                        Log.d(TAG, "Products by category loaded: " + productList.size());
                    } else {
                        showError("Error: " + productResponse.getMessage());
                    }
                } else {
                    showError("Error: " + response.message());
                }
                swipeRefreshLayout.setRefreshing(false);
                showProgress(false);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "API call failed", t);
                swipeRefreshLayout.setRefreshing(false);
                showProgress(false);
            }
        });
    }

    private void searchProducts(String query) {
        showProgress(true);
        Call<ProductResponse> call = apiService.searchProducts(query);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    if (productResponse.isSuccess() && productResponse.getData() != null) {
                        productList = productResponse.getData();
                        productAdapter.updateData(productList);
                        Log.d(TAG, "Search results: " + productList.size());
                    } else {
                        showError("Error: " + productResponse.getMessage());
                    }
                } else {
                    showError("Error: " + response.message());
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                Log.e(TAG, "API call failed", t);
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}