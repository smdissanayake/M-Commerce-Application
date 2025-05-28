package lk.sheha.agriconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;
import lk.sheha.agriconnect.model.PaymentitemList;
import lk.sheha.agriconnect.model.Chackout_productAdapter;
import lk.sheha.agriconnect.model.Product;

public class PaymentActivity extends AppCompatActivity {
    private static final int PAYHERE_REQUEST = 11010;
    TextView alertshow,orderNumber,orderDate,totnim,totnim2;

    private String oid;
    private FirebaseFirestore db;
    PaymentitemList product;
    private FirebaseAuth mAuth;
    private String Log_user_email,productID;

    Map<String, Object> paymentData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");
        db = FirebaseFirestore.getInstance();
        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
        }

//        Intent ii  =   getIntent();
//        Log.i("hellow",ii.getStringExtra("gggg"));

        orderNumber = findViewById(R.id.tvOrderNumber);
        orderDate = findViewById(R.id.tvOrderDate);
        totnim = findViewById(R.id.tvTotalAmount);
        totnim2 = findViewById(R.id.textView42);
//        alertshow = findViewById(R.id.alertshow);

        product = (PaymentitemList) getIntent().getSerializableExtra("seller");
        Log.i("hellow",product.getName());
        Log.i("hellow",product.getQut());
        Log.i("hellow",product.getTitle());
        Log.i("hellow",product.getImageuri());
        Log.i("hellow",product.getPrice());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);

        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 20;
                outRect.bottom = 20;
            }
        };


        oid =  generateOrderNumber();
        Log.i("hellow","no case");
        String price = product.getPrice();
        price = price.replace("Rs ", "");
        double v = Double.parseDouble(price);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(new Date());

        orderNumber.setText("Order Number: "+oid);
        orderDate.setText("Order Date: "+formattedDate);
        totnim.setText("Total Amount: Rs "+price);
        totnim2.setText("LKR "+price);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<PaymentitemList> itemspayment = new ArrayList<>();
        itemspayment.add(new PaymentitemList(product.getName(),product.getTitle(),product.getQut(),product.getPrice(),product.getImageuri(),product.getIsdriver()));

       Chackout_productAdapter adapter = new Chackout_productAdapter(itemspayment,this);
       recyclerView.setAdapter(adapter);


        Button buttonP = findViewById(R.id.buttonP);
        buttonP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InitRequest req = new InitRequest();
                req.setMerchantId("1221813");       // Merchant ID
                req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                req.setAmount(v);             // Final Amount to be charged
                req.setOrderId(oid);        // Unique Reference ID
                req.setItemsDescription("Door bell wireless");  // Item description title
                req.setCustom1("This is the custom message 1");
                req.setCustom2("This is the custom message 2");
                req.getCustomer().setFirstName("Saman");
                req.getCustomer().setLastName("Perera");
                req.getCustomer().setEmail("samanp@gmail.com");
                req.getCustomer().setPhone("+94761784000");
                req.getCustomer().getAddress().setAddress("No.1, Galle Road");
                req.getCustomer().getAddress().setCity("Colombo");
                req.getCustomer().getAddress().setCountry("Sri Lanka");


                Intent intent = new Intent(PaymentActivity.this, PHMainActivity.class);
                intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                startActivityForResult(intent, PAYHERE_REQUEST); //unique request ID e.g. "11001"
            }
        });

        if(product.getIsdriver().equals("isDriver")){
            Log.i("hellow","driver");
//            Intent i = getIntent();
        }else {
            Log.i("hellow","seller");
        }


        db.collection("products")
                .whereEqualTo("title", product.getTitle())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot docs : task.getResult()) {  // Use getDocuments()
                                Log.i("Firestore", "Search OK");
                                Log.i("Firestore", "Document ID: " + docs.getId());
                                productID = docs.getId();
                                // Example: Get data
                                String seller = docs.getString("seller");
                                Log.i("Firestore", "Seller: " + seller);
                            }
                        } else {
                            Log.e("Firestore", "Error getting documents", task.getException());
                        }
                    }
                });




    }

    private void savePaymentToFirestore(StatusResponse statusResponse) {
        paymentData = new HashMap<>();
        paymentData.put("paymentId",oid);
        paymentData.put("status", statusResponse.getStatus());
        paymentData.put("message", statusResponse.getMessage());
        paymentData.put("amount", String.valueOf(product.getPrice()));
        paymentData.put("currency", statusResponse.getCurrency());
        paymentData.put("timestamp", String.valueOf(System.currentTimeMillis()));
        paymentData.put("Quantity", product.getQut().toString());
        paymentData.put("productId", productID);

        db.collection("payments")
                .document(Log_user_email)
                .set(paymentData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Payment successfully added!");
                    showAlert("Payment Successful", "Transaction recorded successfully!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error saving payment", e);
                    showAlert("Error", "Failed to save transaction: " + e.getMessage());
                });

        if(product.getIsdriver().equals("isDriver")){
            Log.i("hellow","driver");


            db.collection("myRequest")
                    .whereEqualTo("hireowner",Log_user_email)
                    .whereEqualTo("product",productID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot document:task.getResult()){

                            DocumentReference washingtonRef = db.collection("myRequest").document(document.getId());
                            washingtonRef
                                    .update("paymentStatus", "paid")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("hellow", "DocumentSnapshot successfully updated!");
                                        }
                                    });
                        }
                    }
                }
            });
        }else {
            Log.i("hellow","seller");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            if (resultCode == Activity.RESULT_OK) {
                if (response != null && response.isSuccess()) {
                    StatusResponse statusResponse = response.getData();
                    savePaymentToFirestore(statusResponse);
                } else {
                    showAlert("Payment Failed", response != null ? response.toString() : "No response received");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                showAlert("Payment Canceled", response != null ? response.toString() : "User canceled the request");
            }
        }
    }

    public static String generateOrderNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000);

        return "ORD" + timestamp + randomNum;
    }
    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}