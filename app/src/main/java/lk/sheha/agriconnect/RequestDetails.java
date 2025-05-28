package lk.sheha.agriconnect;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import lk.sheha.agriconnect.model.NotifiItem;
import lk.sheha.agriconnect.model.Product;
import lk.sheha.agriconnect.model.ReqestItem;

public class RequestDetails extends AppCompatActivity {
    private TextView hr,hr_rate,rq_total,req_des,req_number,stetusText;
    private Button button_call,button_Accept,button_Reject;
    private  String pickuplat,pickuplang,hr_p_rate,requestId,nowstatus;
    private static final int REQUEST_CALL_PHONE = 1;
    private NotifiItem notifi_items;
    private FirebaseFirestore db;
    private Float totp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_details);
        initial();
        notifi_items = (NotifiItem) getIntent().getSerializableExtra("notifi_data");

        Log.i("hellow",notifi_items.getMob());
        SupportMapFragment mapFragment = new SupportMapFragment();

        db = FirebaseFirestore.getInstance();
        db.collection("products").document(notifi_items.getProduct_Id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot result = task.getResult();
                    pickuplat = result.getString("pickupLocationlat");
                    pickuplang = result.getString("pickupLocationlang");
                    hr_p_rate = result.getString("price");
                    hr_rate.setText(hr_p_rate);

                    String numericStr = hr_p_rate.replace("Rs ", "").trim();
                    totp =Float.parseFloat(numericStr)*Float.parseFloat(notifi_items.getHours());

                    rq_total.setText("Rs "+String.valueOf(totp));

                }
            }
        });
        db.collection("deliver_requests")
                .whereEqualTo("Product_Id",notifi_items.getProduct_Id()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    requestId = document.getId(); // Get document ID
                    nowstatus = document.getString("status");
                    stetusText.setText(nowstatus);
                    if(nowstatus.equals("accept")){
                    Log.i("hellow","btnhide");
                        button_Accept.setVisibility(View.GONE);
                        button_Reject.setVisibility(View.GONE);
                        stetusText.setVisibility(View.VISIBLE);

                    }else {
                        stetusText.setVisibility(View.GONE);
                        button_Accept.setVisibility(View.VISIBLE);
                        button_Reject.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.req_drop_location,mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Float lat = Float.parseFloat(notifi_items.getDroplat());
                Float lang= Float.parseFloat(notifi_items.getDroplang());
                Float pikpickuplat = Float.parseFloat(pickuplat);
                Float pikpickuplang = Float.parseFloat(pickuplang);

                LatLng drop = new LatLng(lat, lang);
                LatLng pick = new LatLng(pikpickuplat,pikpickuplang);

                googleMap.addMarker(new MarkerOptions().position(drop).title("Drop Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drop, 10));
                googleMap.addMarker(new MarkerOptions().position(pick).title("Pickup Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pick, 10));
            }
        });

        hr.setText(notifi_items.getHours());
        req_number.setText(notifi_items.getMob());
        req_des.setText(notifi_items.getDescription());
        button_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });




            button_Accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button_Reject.setEnabled(false);
                    Log.i("Firestore",nowstatus);
                    db.collection("deliver_requests").document(requestId)
                            .update("status", "accept")
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Status updated to 'accept'"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating status", e));


                    if(!nowstatus.equals("accept")) {

                        Log.i("hellow", "gk");
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String timeString = sdf.format(new Date());

                        Map<String, Object> test = new HashMap();
                        test.put("Fromuser", notifi_items.getTo());
                        test.put("hireowner", notifi_items.getFrom());
                        test.put("requestId", requestId);
                        test.put("paymentStatus", "pending");
                        test.put("amount", totp.toString());
                        test.put("seen", "no");
                        test.put("product", notifi_items.getProduct_Id());
                        test.put("time", timeString);
                        test.put("Hours",notifi_items.getHours());//gg

//
//
                        db.collection("myRequest")
                                .add(test);
                    }
//                    }

                    showConfirmDialog();
                }
            });

            button_Reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("deliver_requests").document(requestId)
                            .update("status", "reject")
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Status updated to 'accept'"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating status", e));
                }
            });





    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(RequestDetails.this)
                .setTitle("Confirm Request")
                .setMessage("Do you want to accept this ride request?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Handle Yes click
                    Toast.makeText(RequestDetails.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                    finish();
                })
//                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Handle No click
                .show();
    }

    private void initial(){
        hr =findViewById(R.id.hr);
        hr_rate = findViewById(R.id.hr_rate);
        rq_total= findViewById(R.id.rq_total);
        req_des = findViewById(R.id.req_des);
        req_number = findViewById(R.id.req_number);

        button_call= findViewById(R.id.button_call);
        button_Accept = findViewById(R.id.button_Accept);
        button_Reject = findViewById(R.id.button_Reject);
        stetusText = findViewById(R.id.textView46);

    }

    private void makePhoneCall() {
        String phoneNumber = notifi_items.getMob();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE);
        } else {
            startCall(phoneNumber);
        }
    }

    private void startCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}