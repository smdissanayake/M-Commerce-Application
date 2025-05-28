package lk.sheha.agriconnect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class SelLocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private TextView latv,langv;
    private Marker currentMarker;
    LatLng myLocation;

    private Button button_set_new_location,button_set_location_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sel_location);

        SupportMapFragment mapFragment = new SupportMapFragment();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(SelLocationActivity.this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.set_location_layout1,mapFragment);
        fragmentTransaction.commit();

        latv = findViewById(R.id.textLat);
        langv = findViewById(R.id.textLang);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                if(checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
//                        googleMap.setMyLocationEnabled(true);

                        button_set_location_data = findViewById(R.id.button_set_location_data);
                        button_set_location_data.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(SelLocationActivity.this, AddProductActivity.class);
                                if(!latv.getText().equals("0.00")&&!langv.getText().equals("0.00")){
                                    i.putExtra("lat",latv.getText().toString());
                                    i.putExtra("lang",langv.getText().toString());
                                    Log.i("hellow","GG");
                                    startActivity(i);
                                    overridePendingTransition(R.anim.slide_out_left,R.anim.slide_in_right);
                                }
                            }
                        });

                        button_set_new_location = findViewById(R.id.button_set_new_location);
                        button_set_new_location.setOnClickListener(new View.OnClickListener() {
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
                                        latv.setText(String.valueOf(latLng.latitude));
                                        langv.setText(String.valueOf( latLng.longitude));
                                    }
                                });

                            }
                        });

                        Button button_set_current_location = findViewById(R.id.button_set_current_location_send);//gg
                        button_set_current_location.setOnClickListener(new View.OnClickListener() {
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
//


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
                    latv.setText(String.valueOf(latitude));
                    langv.setText(String.valueOf(longitude));

                    myLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                } else {
                    Log.i("hellow", "Failed to get location");
                }
            }
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