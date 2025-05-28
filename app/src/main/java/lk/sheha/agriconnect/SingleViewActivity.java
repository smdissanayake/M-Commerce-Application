package lk.sheha.agriconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import lk.sheha.agriconnect.Helper.UserDatabaseHelper;
import lk.sheha.agriconnect.model.PaymentitemList;
import lk.sheha.agriconnect.model.Product;

public class SingleViewActivity extends AppCompatActivity {
    TextView singlepTitle,qty,price,outoftext,avilbleproduct,seller,txtTotal, txtAvailableStock,description;
    ImageView productImageView,btnIncrease, btnDecrease;
    LinearLayout verifiedcontainer;
    Button buyButton,Pre_order,Send_Reqest;
    EditText edtQuantity;
    double pricedob;
    int qtyint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_view);


        Product product = (Product) getIntent().getSerializableExtra("product");
        initial();
        if (product != null) {

            if(!product.getVerified()){
                verifiedcontainer.setVisibility(View.INVISIBLE);
            }
            singlepTitle.setText(product.getTitle());

            if(product.getCategory().equals("Rent Vehicle")){
                price.setText(product.getPrice()+" /hr");
            }else {
                price.setText(product.getPrice()+" /Kg");
            }

            if(Integer.parseInt(product.getQuantity())==0){
                avilbleproduct.setVisibility(View.GONE);
                outoftext.setVisibility(View.VISIBLE);
            }

            if(product.getCategory().equals("Rent Vehicle")){
                qty.setText(product.getQuantity()+" /Maximum Rent Hovres ");
                price.setText(product.getPrice()+" /hr");
                Pre_order.setVisibility(View.GONE);
                buyButton.setVisibility(View.GONE);
                Send_Reqest.setVisibility(View.VISIBLE);


            }else if(product.getCategory().equals("Pre Order")){
                qty.setText(product.getQuantity()+" /Kg Left");
                price.setText(product.getPrice()+" /Kg");
                Pre_order.setVisibility(View.VISIBLE);
                buyButton.setVisibility(View.GONE);
                Send_Reqest.setVisibility(View.GONE);

            }else {
                qty.setText(product.getQuantity()+" /Kg Left");
                price.setText(product.getPrice()+" /Kg");
                Pre_order.setVisibility(View.GONE);
                buyButton.setVisibility(View.VISIBLE);
                Send_Reqest.setVisibility(View.GONE);
            }

            description.setText(product.getDescription());

            Send_Reqest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(SingleViewActivity.this, SendReqest.class);
                    i.putExtra("c_product",product);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            Glide.with(this).load(product.getImageUrl()).into(productImageView);

            seller.setText(product.getSeller());

            SupportMapFragment mapFragment = new SupportMapFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.piclovcationmap,mapFragment);
            fragmentTransaction.commit();

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    Float lat = Float.parseFloat(product.getPickupLocationlat());
                    Float lang= Float.parseFloat(product.getPickupLocationlang());
                    // Set Location (Example: Colombo, Sri Lanka)
                    LatLng colombo = new LatLng(lat, lang);

                    // Add a marker at Colombo and move camera
                    googleMap.addMarker(new MarkerOptions().position(colombo).title("Picup Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombo, 12));
                }
            });


//            String pprice = ;
            pricedob = Double.parseDouble(product.getPrice().replace("Rs ", "").trim());
//             qtyint =  Integer.parseInt(product.getQuantity());
             qtyint =  1;
            updateTotal();

            btnIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("hellow","hh");
                    if (qtyint<Integer.parseInt(product.getQuantity())){
                        qtyint++;
                        edtQuantity.setText(String.valueOf(qtyint));
                        updateTotal();

                    }

                }
            });

            btnDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("hellow","hhhhhhh");
                    if (qtyint > 1) {
                        qtyint--;
                        edtQuantity.setText(String.valueOf(qtyint));
                        updateTotal();

                    }
                }
            });

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            PaymentitemList plist =  new PaymentitemList(product.getSeller(),product.getTitle(),String.valueOf(qtyint),txtTotal.getText().toString().trim(),product.getImageUrl(),"isSeller");
                    Intent i = new Intent(SingleViewActivity.this,PaymentActivity.class);
                   i.putExtra("seller",plist);
//                   i.putExtra("current","gk");
                   startActivity(i);
                }
            });
            edtQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()) {
                        int enteredQty = Integer.parseInt(s.toString());

                        // Validate against available stock
                        if (enteredQty > Integer.parseInt(product.getQuantity())) {
                            edtQuantity.setText(String.valueOf(Integer.parseInt(product.getQuantity())));
                            edtQuantity.setSelection(edtQuantity.getText().length());
                            qtyint = Integer.parseInt(product.getQuantity());
                        } else if (enteredQty < 1) {
                            edtQuantity.setText("1");
                            edtQuantity.setSelection(edtQuantity.getText().length());
                            qtyint = 1;
                        } else {
                            qtyint = enteredQty;
                        }
                        updateTotal();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });


        }
    }

    private void updateTotal() {
        int totalPrice = (int) (qtyint * pricedob); // Convert to Integer
        txtTotal.setText("Rs " + totalPrice);
    }

    private void initial(){
        productImageView = findViewById(R.id.single_imageView);
        verifiedcontainer = findViewById(R.id.verifiedcontainer);
        singlepTitle = findViewById(R.id.singlepTitle);
        price= findViewById(R.id.price);
        outoftext = findViewById(R.id.outoftext);
        qty = findViewById(R.id.textView25);
        seller =findViewById(R.id.seller);
        buyButton = findViewById(R.id.button_buy_now);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        edtQuantity = findViewById(R.id.edtQuantity);
        txtTotal =findViewById(R.id.txtTotal);
        Send_Reqest = findViewById(R.id.Send_Reqest);
        Pre_order = findViewById(R.id.Pre_order);
        description = findViewById(R.id.description);
    }
}