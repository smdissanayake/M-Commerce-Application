package lk.sheha.agriconnect;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import lk.sheha.agriconnect.Helper.UserDatabaseHelper;
import lk.sheha.agriconnect.model.Product;
import lk.sheha.agriconnect.model.ProductAdapter;

public class HomeFragment extends Fragment {
    public RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private String Log_user_email,log_user_username;
    private ImageView profileimage,wishbtn,carticon;
    private FirebaseAuth mAuth;
    private TextView username,useremail;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration,listenerRegistration2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = getActivity().getSharedPreferences("app_prefs",Context.MODE_PRIVATE);
        String u = preferences.getString("email","");

        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
            log_user_username = user.getDisplayName();
        }
        Log.i("hellow",Log_user_email);

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(rootView.getContext());
        Cursor  c1 =  dbHelper.getUserByEmail(Log_user_email);


        if(c1.moveToFirst()){
            Log.i("hellow","db user"+ c1.getString(c1.getColumnIndexOrThrow("email")));
        }else {
            Log.i("hellow","gg");
        }



        mAuth = FirebaseAuth.getInstance();


        db = FirebaseFirestore.getInstance();
        profileimage = rootView.findViewById(R.id.profileimage);
        db.collection("user").document(Log_user_email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains("profileImage")) {
                            String imageUrl = document.getString("profileImage");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(rootView.getContext()).load(imageUrl).into(profileimage);
                            }
                        }
                    }
                }
            }
        });

        // Initialize Google Sign-In client
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(),
                new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build());


        useremail =  rootView.findViewById(R.id.textView6);
        username =  rootView.findViewById(R.id.textView10);
        username.setText(Log_user_email);
        useremail.setText(log_user_username);



        wishbtn = rootView.findViewById(R.id.wishbtn);
        carticon = rootView.findViewById(R.id.carticon);
        wishbtn.setOnClickListener(v -> {
            signOut();
        });

        carticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                signOut();
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        productList = new ArrayList<>();
//        Recycler View
        recyclerView = rootView.findViewById(R.id.recycleView);
//        Grid Paddinge
        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 20; // Horizontal margin
                outRect.right = 20;
                outRect.top = 20;
                outRect.bottom = 20;
            }
        };


        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columns

        productList = new ArrayList<>();

        adapter = new ProductAdapter(requireContext(), productList);
        recyclerView.setAdapter(adapter);



        db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereNotIn("seller", Arrays.asList(Log_user_email))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
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

                            // Log the fetched data
                            Log.i("FirestoreData", "ID: " + id +
                                    " Title: " + title +
                                    " Price: " + price +
                                    " Description: " + description +
                                    " ImageURL: " + imageUrl +
                                    " Category: " + category +
                                    " Province: " + province +
                                    " Pickup Lat: " + pickupLocationLat +
                                    " Pickup Lng: " + pickupLocationLng +
                                    " Payment Method: " + paymentMethod +
                                    " Pickup Date: " + pickupDate +
                                    " Vehicle Type: " + vehicleType +
                                    " Capacity: " + capacity +
                                    " Seller: " + seller +
                                    " Verified: " + verified +
                                    " Quantity: " + quantity);

                            Product product = new Product(id, category, province, pickupLocationLat, pickupLocationLng,
                                    pickupDate, paymentMethod, vehicleType, capacity, seller,
                                    title, price, description, imageUrl, quantity, verified);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents", task.getException());
                    }
                });
        listenForRequestUpdates(Log_user_email);

        return rootView;

    }
    private void signOut() {

        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration2.remove();
            listenerRegistration = null;
            listenerRegistration2 = null;
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Logging out...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log_user_email =null; //gamakawaa gg

        mAuth.signOut();


        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {

            SharedPreferences preferences = requireActivity().getSharedPreferences("app_prefs", requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("is_googlelod", false);
            editor.putBoolean("is_manuallod", false);
            editor.putString("email", null);
            editor.apply();


            progressDialog.dismiss();


            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
    public void listenForRequestUpdates(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            Log.e("Firestore", "Error: User ID is null or empty.");
            return;
        }

        listenerRegistration =  db.collection("deliver_requests")
                .where(
                        Filter.or(
                                Filter.equalTo("from",Log_user_email),
                                Filter.equalTo("to",Log_user_email)
                        )
                )
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.d("Firestore", "No matching ride requests found.");
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            DocumentSnapshot document = dc.getDocument();
                            String status = document.getString("status");

                            Log.d("Firestore", "Ride Status Updated: " + status);

                           String fromuser =   document.getString("from");
                           if(fromuser.equals(Log_user_email)){
                               showNotification("Ride Update", "Your ride status: " + status);
                           }
                            // Show notification to user
                        }
                    }
                });



//        ssssssssssssssssssssssssssssss
        listenerRegistration2 =  db.collection("myRequest")
//                .where(
//                        Filter.or(
//                                Filter.equalTo("Fromuser",Log_user_email),
//                                Filter.equalTo("to",Log_user_email)
//                        )
//                )
                .whereEqualTo("Fromuser",Log_user_email)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.d("Firestore", "No matching ride requests found.");
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            DocumentSnapshot document = dc.getDocument();
                            String status = document.getString("paymentStatus");

                            Log.d("Firestore", "Ride Status Updated: " + status);

                            String fromuser =   document.getString("from");
                            if(fromuser.equals(Log_user_email)){
                                showNotification("Ride Update", "Your ride status: " + status);
                            }
                            // Show notification to user
                        }
                    }
                });
    }

private void showNotification(String title, String message) {
    NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
    String channelId = "ride_updates_channel";

    // Create a notification channel (required for Android 8+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel channel = new NotificationChannel(
                channelId,
                "Ride Updates",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationManager.createNotificationChannel(channel);
    }

    // Build notification
    NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.icon_notification) // Change to your app's notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

    // Show notification
    notificationManager.notify(1, builder.build());
}

}

//        allow read, write: if request.time < timestamp.date(2025, 3, 12);