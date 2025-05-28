package lk.sheha.agriconnect;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lk.sheha.agriconnect.model.Product;

public class SendReqest extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button button_set_new_location_send;
    private TextView sendlat,sendlangv;
    private Marker currentMarker;
    LatLng myLocation;
    private TextView sendtextLat,sendtextLang,rqsenddata,reqfromdata,sendiddata,senddatedata;
    private EditText bdesInput,expectHrinput,reqmobilinput;
    private CalendarView calendarView2;
    private Button button_send_request;
    private Product product;

    String seller,pid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_reqest);

        product = (Product) getIntent().getSerializableExtra("c_product");
        innitial();
//        setup map
        seller = product.getSeller().toString();
        pid = product.getId().toString();

        SupportMapFragment mapFragment = new SupportMapFragment();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.set_drop_location,mapFragment);
        fragmentTransaction.commit();

        sendlat = findViewById(R.id.sendtextLat);
        sendlangv = findViewById(R.id.sendtextLang);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;

                if(checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
//                        googleMap.setMyLocationEnabled(true);

                        Float lat = Float.parseFloat(product.getPickupLocationlat());
                        Float lang= Float.parseFloat(product.getPickupLocationlang());
                        // Set Location (Example: Colombo, Sri Lanka)
                        LatLng colombo = new LatLng(lat, lang);
                        googleMap.addMarker(new MarkerOptions()
                                .position(colombo)
                                .title("Pickup Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_icon))
                        );
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, 12));

                        button_set_new_location_send = findViewById(R.id.button_set_new_location_send);
                        button_set_new_location_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng latLng) {
                                        myLocation=null;

                                        // Remove previous marker if exists
                                        if (currentMarker != null) {
                                            currentMarker.remove();
                                        }

                                        // Add a new marker at the clicked location
                                        currentMarker = googleMap.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .title("Selected Location"));

                                        // Log the latitude and longitude
                                        Log.i("MAP_CLICK", "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
                                        sendlat.setText(String.valueOf(latLng.latitude));
                                        sendlangv.setText(String.valueOf( latLng.longitude));
                                    }
                                });

                            }
                        });

                        Button button_set_current_location_send = findViewById(R.id.button_set_current_location_send1);
                        button_set_current_location_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getCurrentLocation();
                            }
                        });

                    }else {
                        Log.i("hellow","Fine location denided");
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},100);
                    }
                }else {
                    Log.i("hellow","Coarse location denided");
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);

                }
            }
        });
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.i("hellow", "Lat: " + latitude + ", Lng: " + longitude);
                    sendlat.setText(String.valueOf(latitude));
                    sendlangv.setText(String.valueOf(longitude));

                    myLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                } else {
                    Log.i("hellow", "Failed to get location");
                }
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        button_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String des = bdesInput.getText().toString().trim();
                String hr =  expectHrinput.getText().toString().trim();
                String mob =  reqmobilinput.getText().toString().trim();
                String droplat = sendtextLat.getText().toString().trim();
                String droplang = sendtextLang.getText().toString().trim();

                Map<String, Object> rideRequest = new HashMap<>();
                rideRequest.put("description",des);
                rideRequest.put("Hours",hr);
                rideRequest.put("mob",mob);
                rideRequest.put("droplat",droplat);
                rideRequest.put("droplang",droplang);
//                rideRequest.put("droplang",droplang); gg

                rideRequest.put("to",product.getSeller());
                rideRequest.put("from",mAuth.getCurrentUser().getEmail().toString());
                rideRequest.put("Rent_Date",senddatedata.getText().toString().trim());
                rideRequest.put("Product_Id",product.getId());
                rideRequest.put("status", "pending");

                Log.i("hellow", "Ride Request Data: " + rideRequest.toString());

                db.collection("deliver_requests")
                        .add(rideRequest)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("hellow", "Request Sent: " + documentReference.getId());
                            Toast.makeText(SendReqest.this, "Ride Request Sent!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("hellow", "Error Sending Request", e);
                            Toast.makeText(SendReqest.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                        });


            }
        });
    }


    String selectedDate ;
    private FirebaseAuth mAuth;
    public void innitial(){
        sendtextLat= findViewById(R.id.sendtextLat);
        sendtextLang= findViewById(R.id.sendtextLang);
        bdesInput = findViewById(R.id.bdesInput);
        expectHrinput = findViewById(R.id.expectHrinput);
        reqmobilinput = findViewById(R.id.reqmobilinput);
        rqsenddata = findViewById(R.id.rqsenddata);
        reqfromdata = findViewById(R.id.reqfromdata);
        sendiddata = findViewById(R.id.sendiddata);
        senddatedata = findViewById(R.id.senddatedata);
        calendarView2 = findViewById(R.id.calendarView2);
        button_send_request =findViewById(R.id.button_send_request);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        reqfromdata.setText(user.getEmail().toString().trim());
        rqsenddata.setText(product.getSeller());
        sendiddata.setText(product.getId());

        Log.i("hellow",product.getId());

        calendarView2.setOnDateChangeListener((v, year, month, day) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Log.i("DateSelected", sdf.format(cal.getTime()));
            selectedDate = sdf.format(cal.getTime());
            senddatedata.setText(selectedDate);
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode==100&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permision Denided", Toast.LENGTH_SHORT).show();
        }
        if(requestCode==200&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permision Denided", Toast.LENGTH_SHORT).show();
        }

    }
}