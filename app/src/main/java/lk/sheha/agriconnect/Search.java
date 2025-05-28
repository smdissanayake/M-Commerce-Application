package lk.sheha.agriconnect;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import lk.sheha.agriconnect.model.Product;
import lk.sheha.agriconnect.model.ProductAdapter;
import lk.sheha.agriconnect.model.SeachItemAdapter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;


public class Search extends Fragment {


    private RecyclerView recyclerView;
    private List<Product> productList;
    private SeachItemAdapter productAdapter;
    private FirebaseFirestore db;
    private TextInputEditText searchInput;
    private Button btnBuyNow, btnPreOrder, btnDriver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        recyclerView = rootView.findViewById(R.id.searcView);
        searchInput = rootView.findViewById(R.id.searchInput);
        btnBuyNow = rootView.findViewById(R.id.button1);
        btnPreOrder = rootView.findViewById(R.id.button_pre_order);
        btnDriver = rootView.findViewById(R.id.button3);

        // RecyclerView Setup
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productList = new ArrayList<>();
        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 20;
                outRect.bottom = 20;
            }
        };


        recyclerView.addItemDecoration(decoration);
        productAdapter = new SeachItemAdapter(requireContext(), productList);
        recyclerView.setAdapter(productAdapter);

        // Load all products initially
        fetchProducts(null);

        // Search listener for normal text search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchProducts(s.toString().trim()); // Search as user types
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Category filter buttons
        btnBuyNow.setOnClickListener(v -> fetchProductsByCategory("Now sell"));
        btnPreOrder.setOnClickListener(v -> fetchProductsByCategory("Pre Order"));
        btnDriver.setOnClickListener(v -> fetchProductsByCategory("Rent Vehicle"));

        return rootView;
    }

    private void fetchProducts(@Nullable String searchQuery) {
        CollectionReference productsRef = db.collection("products");

        productsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) return;

                productList.clear();
                for (QueryDocumentSnapshot document : snapshots) {
//                    Product product = doc.toObject(Product.class);
                    String id = document.getId();
                    String title = document.getString("title") != null ? document.getString("title") : "N/A";
                    String price = document.getString("price") != null ? document.getString("price") : "0.00";
                    String description = document.getString("description") != null ? document.getString("description") : "No description";
                    String imageUrl = document.getString("imageUrl") != null ? document.getString("imageUrl") : "";
                    String category = document.getString("category") != null ? document.getString("category") : "Unknown";
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
                    Product product = new Product(id, category, province, pickupLocationLat, pickupLocationLng,
                            pickupDate, paymentMethod, vehicleType, capacity, seller,
                            title, price, description, imageUrl, quantity, verified);

                    // If there is a search query, filter by name
                    if (searchQuery == null || product.getTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                        productList.add(product);
                    }
                }

                productAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchProductsByCategory(String category) {
        CollectionReference productsRef = db.collection("products");

        productsRef.whereEqualTo("category", category).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
//                    Product product = doc.toObject(Product.class);
                    String id = document.getId();
                    String title = document.getString("title") != null ? document.getString("title") : "N/A";
                    String price = document.getString("price") != null ? document.getString("price") : "0.00";
                    String description = document.getString("description") != null ? document.getString("description") : "No description";
                    String imageUrl = document.getString("imageUrl") != null ? document.getString("imageUrl") : "";
                    String cat = document.getString("category") != null ? document.getString("category") : "Unknown";
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
                    Product product = new Product(id, cat, province, pickupLocationLat, pickupLocationLng,
                            pickupDate, paymentMethod, vehicleType, capacity, seller,
                            title, price, description, imageUrl, quantity, verified);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }
        });
    }


//    private RecyclerView recyclerView;
//    private List<Product> productList;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View rootview = inflater.inflate(R.layout.fragment_search, container, false);
//
////        recyclerView.addItemDecoration(decoration);
//        recyclerView = rootview.findViewById(R.id.searcView);
//        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                super.getItemOffsets(outRect, view, parent, state);
//                outRect.top = 10;
//                outRect.bottom = 10;
//            }
//        };
//        recyclerView.addItemDecoration(decoration);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        productList = new ArrayList<>();
//        productList.add(new Product("Apple", R.drawable.p1));
//        productList.add(new Product("Banana", R.drawable.p2));
//        productList.add(new Product("Grapes", R.drawable.p3));
//        productList.add(new Product("Mango", R.drawable.icon_password_reset));
//
//        SeachItemAdapter productAdapter = new SeachItemAdapter(requireContext(), productList);
//        recyclerView.setAdapter(productAdapter);
//
//        return rootview;
//    }
}