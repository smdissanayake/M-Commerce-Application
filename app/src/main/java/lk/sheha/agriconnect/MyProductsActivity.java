package lk.sheha.agriconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.sheha.agriconnect.model.MyProductAdapter;
import lk.sheha.agriconnect.model.Product;

public class MyProductsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private RecyclerView recyclerView;
    private MyProductAdapter adapter;
    private FirebaseAuth mAuth;
    private List<Product> productList;
    private FirebaseFirestore db;
    private String Log_user_email,log_user_username;
    private Map<String, Integer> categoryCounts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_products);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");

        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
        }

        pieChart = findViewById(R.id.pieChart);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        adapter = new MyProductAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadProducts();
        setupSwipeToDelete();
    }

    private void loadProducts() {
        db.collection("products").whereEqualTo("seller",Log_user_email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    productList.clear();
                    categoryCounts.clear();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String id = document.getId();
                        String title = document.getString("title") != null ? document.getString("title") : "N/A";
                        String price = document.getString("price") != null ? document.getString("price") : "0.00";
                        String description = document.getString("description") != null ? document.getString("description") : "No description";
                        String imageUrl = document.getString("imageUrl") != null ? document.getString("imageUrl") : "";
                        String categoryp = document.getString("category") != null ? document.getString("category") : "Unknown";
                        String province = document.getString("province") != null ? document.getString("province") : "Unknown";
                        String pickupLocationLat = document.getString("pickupLocationlat") != null ? document.getString("pickupLocationlat") : "0";
                        String pickupLocationLng = document.getString("pickupLocationlang") != null ? document.getString("pickupLocationlang") : "0";
                        String pickupDate = document.getString("pickupDate") != null ? document.getString("pickupDate") : "N/A";
                        String paymentMethod = document.getString("paymentMethod") != null ? document.getString("paymentMethod") : "Unknown";
                        String vehicleType = document.getString("vehicleType") != null ? document.getString("vehicleType") : "N/A";
                        String capacity = document.getString("capacity") != null ? document.getString("capacity") : "0";
                        String seller = document.getString("seller") != null ? document.getString("seller") : "Unknown";
                        String quantity = document.getString("quantity") != null ? document.getString("quantity") : "0";
                        Boolean verified = document.getBoolean("verified") != null ? document.getBoolean("verified") : false;

                        Product product = new Product(id, categoryp, province, pickupLocationLat, pickupLocationLng,
                                pickupDate, paymentMethod, vehicleType, capacity, seller,
                                title, price, description, imageUrl, quantity, verified);
                        productList.add(product);

                        String category = document.getString("category");
                        if (category != null) {
                            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    setupPieChart();
                }
            } else {
                Log.e("Firestore", "Error getting products", task.getException());
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Do you really want to remove this product?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProduct(position))
                .setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position))
                .setCancelable(false)
                .show();
    }

    private void deleteProduct(int position) {
        Product product = productList.get(position);
        String productId = product.getId();

        db.collection("products").document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting product", e);
                    adapter.notifyItemChanged(position);
                });
    }

    private void setupPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Product Categories");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN});
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextColor(Color.BLACK);

        pieChart.invalidate();
    }
}
