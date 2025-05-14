package com.example.testgiuaky2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Hiển thị HomeFragment khi khởi động ứng dụng
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Sử dụng if-else thay vì switch để tránh lỗi "Constant expression required"
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
        else if (id == R.id.nav_product) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProductListFragment())
                    .commit();
        }

        else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
        }

        else if (id == R.id.nav_cart) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .commit();
        }

        else if (id == R.id.nav_order) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OrderListFragment())
                    .commit();
        }

        else if (id == R.id.nav_logout) {
            // Hiển thị thông báo logout
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();

            // Xóa dữ liệu đăng nhập (ví dụ: SharedPreferences)
            clearUserSession();

            // Khởi động lại ứng dụng
            restartApp();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearUserSession() {
        // Xóa thông tin đăng nhập từ SharedPreferences
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Nếu bạn sử dụng cách lưu trữ khác, hãy xóa dữ liệu tương ứng ở đây
    }

    private void restartApp() {
        // Tạo Intent để khởi động lại Activity chính (hoặc màn hình đăng nhập)
        Intent intent = new Intent(this, MainActivity.class);
        // Xóa tất cả các Activity khác khỏi stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Đóng Activity hiện tại
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}