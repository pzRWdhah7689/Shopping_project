package com.example.shopping_project;

import static com.example.shopping_project.R.id.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bnv;
    RecyclerView rv;
    public static final String PRODUCT_KEY = "product_key";
    private static final int PERMISSION_REQ_CODE = 1;
    public static final String TABLE_NAME_KEY = "table_key";
    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;
    SharedPreferences shp_id;
    SharedPreferences.Editor shpEditor_id;
    public static boolean flag;
    ShoppingDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        flag = false;
        db = new ShoppingDatabase(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
        }

        rv = findViewById(R.id.rv_home);
        shp = getSharedPreferences("myPreferences", MODE_PRIVATE);
        shp_id = getSharedPreferences("Preferences_id", MODE_PRIVATE);

        ArrayList<Products> products1 = db.getAllProducts(ShoppingDatabase.TB_PRODUCT_DISCOUNT);
        HomeAdabter adapter = new HomeAdabter(products1, new OnRecyclerViewClickListener() {
            @Override
            public void OnItemClick(int productId) {
                Intent i = new Intent(getBaseContext(), DisplayProductsActivity.class);
                i.putExtra(PRODUCT_KEY, productId);
                i.putExtra(TABLE_NAME_KEY, ShoppingDatabase.TB_PRODUCT_DISCOUNT);
                flag = true;
                startActivity(i);
            }
        });

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

        bnv = findViewById(R.id.BottomNavigationView);
        bnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                int itemId = item.getItemId();
                if (itemId == R.id.Home) {
                    intent = new Intent(getBaseContext(), HomeActivity.class);
                } else if (itemId == R.id.products) {
                    intent = new Intent(getBaseContext(), MainActivity.class);
                } else if (itemId == profile) {
                    intent = new Intent(getBaseContext(), ProfileActivity.class);
                } else if (itemId == R.id.basket) {
                    intent = new Intent(getBaseContext(), PurchasesActivity.class);
                } else {
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_up_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout) {
            logout();
        } else if (itemId == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        try {
            shpEditor = shp.edit();
            shpEditor.putInt("user", 0);
            shpEditor.apply();

            shpEditor_id = shp_id.edit();
            shpEditor_id.putInt("user_id", 0);
            shpEditor_id.apply();

            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
