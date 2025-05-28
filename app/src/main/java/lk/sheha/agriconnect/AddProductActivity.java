package lk.sheha.agriconnect;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.RowSetListener;

import lk.sheha.agriconnect.model.CatItems;
import lk.sheha.agriconnect.model.dropdownData;

public class AddProductActivity extends AppCompatActivity {

    // UI Components
    private ImageView imageView;
    private Spinner categorySpinner,provinceSpinner, districtSpinner;
    private Map<String, String[]> districtMap;
    private CalendarView calendarView;
    private RadioGroup paymentRadioGroup;
    private TextInputEditText titleInput, descriptionInput, priceInput, quantityInput, districtInput, provinceInput, capacityInput,vehicleTypeSpinner;
    private TextInputLayout capacityLayout,spinner2layout;
    private TextView advancePaymentLabel,Vehicle_Type,vehicleCapacityLabel,Weight_or_Unit_Type,Unit_type;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Uri imageUri;
    private boolean isEditing = false;
    private String selectedCategory = "" ,province,distric,pickupLat,pickupLang,selectedDate,currentUser,Struser;
    private Button button_set_location;
    private SharedPreferences s;
   private int payselectedGenderId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onResume() {
        super.onResume();


        s = getSharedPreferences("a_data",MODE_PRIVATE);
        SharedPreferences.Editor ed = s.edit();


//
//        titleInput.setText(s.getString("Title",null));
//        ed.putStringSet("Title",null);
//        ed.apply();
        RestoreAll();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);


        // Initialize UI components
        initializeViews();
        setupCategorySpinner();
        setupImageSelection();
        setupCalendar();
        setupSubmitButton();
        LoacationDropdown();

//        db = FirebaseFirestore.getInstance();
//        db.collection("user")
//                .document()
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//
//                }
//            }
//        });

        SharedPreferences preferences = getSharedPreferences("app_prefs",Context.MODE_PRIVATE);
        String u = preferences.getString("email","");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentUser = user.getEmail();
        Log.i("hellow","cuser"+currentUser.toString());

        if(!u.isEmpty()){
            Log.i("hellow","product_user"+u);
        }else if(user != null) {
            Log.i("hellow",user.getEmail());
        }else {
            Log.i("hellow","nouser Found");
        }

        Intent i =getIntent();
        pickupLat = i.getStringExtra("lat");
        pickupLang = i.getStringExtra("lang");
        Log.i("hellow",pickupLat+" "+pickupLang);
        priceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEditing) return;

                isEditing = true;
                String input = editable.toString().replaceAll("[^\\d]", ""); // Remove non-numeric characters
                if (!input.isEmpty()) {
                    double parsed = Double.parseDouble(input) / 100.0; // Convert to decimal
                    DecimalFormat formatter = new DecimalFormat("Rs ##0.00");  //efmlemfksmskksfkskfksnfknsknskdnksnf
                    priceInput.setText(formatter.format(parsed));
                    priceInput.setSelection(priceInput.getText().length()); // Move cursor to the end
                }
                isEditing = false;
            }
        });

        button_set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedata();
                startActivity(new Intent(AddProductActivity.this, SelLocationActivity.class));
            }
        });
    }

    private void initializeViews() {
        categorySpinner = findViewById(R.id.spinner);
        vehicleTypeSpinner = findViewById(R.id.spinner2);
        calendarView = findViewById(R.id.calendarView);
        paymentRadioGroup = findViewById(R.id.paymenoption);
        imageView = findViewById(R.id.imageView);

        titleInput = findViewById(R.id.TitleInput);
        descriptionInput = findViewById(R.id.DescriptionInput);
        priceInput = findViewById(R.id.PriceInput);
        quantityInput = findViewById(R.id.QuantityInput);
        districtInput = findViewById(R.id.emailInput);
//        provinceInput = findViewById(R.id.proivinceInput);
        capacityInput = findViewById(R.id.CapacityInput);
        spinner2layout = findViewById(R.id.spinner2layout);
        advancePaymentLabel = findViewById(R.id.advpay);

        Weight_or_Unit_Type= findViewById(R.id.Weight_or_Unit_Type);
        Vehicle_Type =findViewById(R.id.Vehicle_Type);
        vehicleCapacityLabel = findViewById(R.id.lable_Vehicle_Capacity);
        capacityLayout = findViewById(R.id.CapacityLayout);

        Unit_type = findViewById(R.id.Unit_type);
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);

        button_set_location = findViewById(R.id.button_set_location);
    }

    private void setupCategorySpinner() {
        ArrayList<CatItems> itemList = new ArrayList<>();
        itemList.add(new CatItems("Now sell", "Instant sale", R.drawable.icon_buyer));
        itemList.add(new CatItems("Pre Order", "Future orders", R.drawable.icon_farmer_drodown));
        itemList.add(new CatItems("Rent Vehicle", "Vehicle rental", R.drawable.icon_driver));

        AraaAdapter adapter = new AraaAdapter(this, R.layout.dropdown_cat_layout, itemList);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = ((CatItems) parent.getItemAtPosition(position)).getName();
                Log.i("hellow",selectedCategory);
                toggleUIBasedOnCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });
    }

    private void LoacationDropdown(){
        List<String> provinceList = new ArrayList<>();
        provinceList.add("Select your province");
        provinceList.add("Central");
        provinceList.add("Eastern");
        provinceList.add("Northern");
        provinceList.add("Southern");
        provinceList.add("Western");
        provinceList.add("North Western");
        provinceList.add("North Central");
        provinceList.add("Uva");
        provinceList.add("Sabaragamuwa");

        districtMap = new HashMap<>();
        districtMap.put("Central", new String[]{"Kandy", "Matale", "Nuwara Eliya"});
        districtMap.put("Eastern", new String[]{"Trincomalee", "Batticaloa", "Ampara"});
        districtMap.put("Northern", new String[]{"Jaffna", "Kilinochchi", "Mannar", "Vavuniya", "Mullaitivu"});
        districtMap.put("Southern", new String[]{"Galle", "Matara", "Hambantota"});
        districtMap.put("Western", new String[]{"Colombo", "Gampaha", "Kalutara"});
        districtMap.put("North Western", new String[]{"Kurunegala", "Puttalam"});
        districtMap.put("North Central", new String[]{"Anuradhapura", "Polonnaruwa"});
        districtMap.put("Uva", new String[]{"Badulla", "Monaragala"});
        districtMap.put("Sabaragamuwa", new String[]{"Ratnapura", "Kegalle"});

        // Province Spinner Adapter
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceList);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        // Initialize District Spinner with Default Message
        updateDistrictSpinner(null);

        // Handle Province Selection
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    updateDistrictSpinner(null); // Show default district message
                } else {
                    updateDistrictSpinner(selectedProvince);
                    province= selectedProvince;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Handle District Selection (Optional Toast for Testing)
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = parent.getItemAtPosition(position).toString();
                if (!selectedDistrict.equals("Select your district")) {
                    Toast.makeText(AddProductActivity.this, "Selected District: " + selectedDistrict, Toast.LENGTH_SHORT).show();
                    distric = selectedDistrict;

                    Toast.makeText(AddProductActivity.this, province+" "+distric, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void updateDistrictSpinner(String province) {
        List<String> districtList = new ArrayList<>();
        districtList.add("Select your district"); // Default option

        if (province != null && districtMap.containsKey(province)) {
            String[] districts = districtMap.get(province);
            for (String district : districts) {
                districtList.add(district);
            }
        }

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);
    }
    private void toggleUIBasedOnCategory() {
        switch (selectedCategory) {
            case "Now sell":
                Log.i("hello","waw");
                // Hide calendar, payment, and vehicle sections
                Unit_type.setText("/ Kg");
                calendarView.setVisibility(View.GONE);
                paymentRadioGroup.setVisibility(View.GONE);
                advancePaymentLabel.setVisibility(View.GONE);
                vehicleCapacityLabel.setVisibility(View.GONE);
                capacityLayout.setVisibility(View.GONE);
                capacityInput.setVisibility(View.GONE);
                Weight_or_Unit_Type.setVisibility(View.GONE);
                vehicleTypeSpinner.setVisibility(View.GONE);
                Vehicle_Type.setVisibility(View.GONE);
                spinner2layout.setVisibility(View.GONE);
                break;

            case "Pre Order":
                // Show calendar and payment, hide vehicle
                Unit_type.setText("/ Kg");
                calendarView.setVisibility(View.VISIBLE);
                paymentRadioGroup.setVisibility(View.VISIBLE);
                advancePaymentLabel.setVisibility(View.VISIBLE);
                vehicleCapacityLabel.setVisibility(View.GONE);
                capacityLayout.setVisibility(View.GONE);
                Weight_or_Unit_Type.setVisibility(View.GONE);
                vehicleTypeSpinner.setVisibility(View.GONE);
                Vehicle_Type.setVisibility(View.GONE);
                spinner2layout.setVisibility(View.GONE);
                capacityInput.setVisibility(View.GONE);
                break;

            case "Rent Vehicle":
                // Show vehicle, hide calendar and payment
                Unit_type.setText("/ hr");
                calendarView.setVisibility(View.VISIBLE);
                paymentRadioGroup.setVisibility(View.GONE);
                advancePaymentLabel.setVisibility(View.GONE);
                vehicleCapacityLabel.setVisibility(View.VISIBLE);
                capacityLayout.setVisibility(View.VISIBLE);
                capacityInput.setVisibility(View.VISIBLE);
                Weight_or_Unit_Type.setVisibility(View.GONE);
                vehicleTypeSpinner.setVisibility(View.VISIBLE);
                Vehicle_Type.setVisibility(View.VISIBLE);
                spinner2layout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setupImageSelection() {
        Button selectImageButton = findViewById(R.id.selectImageButton);
        selectImageButton.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageView);
            Log.i("ImagePicker", "Image selected: " + imageUri.toString());
        } else {
            Log.e("ImagePicker", "Image selection failed.");
        }
    }

    private void setupCalendar() {
        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Log.i("DateSelected", sdf.format(cal.getTime()));
            selectedDate = sdf.format(cal.getTime());
        });
    }

    private void setupSubmitButton() {
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                logAllInputs();

                Toast.makeText(this, "New Product Added", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private boolean validateInputs() {




        if (titleInput.getText().toString().trim().isEmpty()) {
            titleInput.setError("Title required");
            return false;
        }
        if (descriptionInput.getText().toString().trim().isEmpty()) {
            descriptionInput.setError("Description required");
            return false;
        }
        if (priceInput.getText().toString().trim().isEmpty()) {
            priceInput.setError("Price required");
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Image required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(quantityInput.getText().toString().trim().isEmpty()){
            quantityInput.setError("Price required");
            return false;
        }
        payselectedGenderId = paymentRadioGroup.getCheckedRadioButtonId();

        if(paymentRadioGroup.getVisibility() != View.GONE){
            if (payselectedGenderId == -1) {
                Toast.makeText(this, "Payment Option required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(calendarView.getVisibility() != View.GONE){
            if(selectedDate==null){
                Toast.makeText(this, "Pickup Date required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if(capacityInput.getVisibility()!=View.GONE){
            if(capacityInput.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Capacity required", Toast.LENGTH_SHORT).show();
                capacityInput.setError("Capacity required");
                return false;
            }
        }

        if(vehicleTypeSpinner.getVisibility() != View.GONE){
            if (vehicleTypeSpinner.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Vehicle Type required", Toast.LENGTH_SHORT).show();
                vehicleTypeSpinner.setError("Vehicle Type required");
                return false;
            }
        }

        return true;
    }

    private void savedata(){
        SharedPreferences.Editor editor = s.edit();

        if (!titleInput.getText().toString().trim().isEmpty()) {
            editor.putString("Title",titleInput.getText().toString());
            editor.apply();
        }
        if (!descriptionInput.getText().toString().trim().isEmpty()) {
            editor.putString("Description",descriptionInput.getText().toString());
            editor.apply();
        }
//        if (!priceInput.getText().toString().trim().isEmpty()) {
//            editor.putString("Price",priceInput.getText().toString());
//            editor.apply();
//        }
//        if(!quantityInput.getText().toString().trim().isEmpty()){
//            editor.putString("Quantity",quantityInput.getText().toString());
//            editor.apply();
//        }
    }

    private void RestoreAll(){
        SharedPreferences.Editor editor = s.edit();

            titleInput.setText(s.getString("Title",null));

            editor.apply();


            descriptionInput.setText(s.getString("Description",null));

            editor.apply();


//            priceInput.setText(Integer.parseInt(s.getString("Price",null)));
//            editor.putString("Price",null);
//            editor.apply();


//            quantityInput.setText(Integer.parseInt(s.getString("Quantity",0)));
//            editor.putString("Quantity",null);
//            editor.apply();
    }

    private void logAllInputs() {
        Log.i("FormData", "Category: " + selectedCategory);
        Log.i("FormData", "Des: " + descriptionInput.getText().toString());
        Log.i("FormData", "Title: " + titleInput.getText());
        Log.i("FormData", "price input: " + priceInput.getText());
        Log.i("FormData", "image Uri: " + imageUri.toString());
        Log.i("FormData", "qutity: " + quantityInput.getText().toString());
        if(provinceSpinner.getSelectedItemId()!=0){
            Log.i("FormData","distric" + provinceSpinner.getSelectedItem().toString());
        }
//        if(districtSpinner.getSelectedItemId()!=0){
//            Log.i("FormData","distric" + districtSpinner.getSelectedItem().toString());
//        }
        Log.i("FormData", "Picup location: " + pickupLat+" "+pickupLang);

        if(calendarView.getVisibility() != View.GONE){
            Log.i("FormData", "Picup date: " + selectedDate);
        }
        if(paymentRadioGroup.getVisibility()!=View.GONE){
            RadioButton selectedGender = findViewById(payselectedGenderId);
            String gender = selectedGender != null ? selectedGender.getText().toString() : "";
            Log.i("FormData", "adv preq: " +gender);
        }
        if(vehicleTypeSpinner.getVisibility() != View.GONE){
            Log.i("FormData","vehical type: " +vehicleTypeSpinner.getText().toString().trim());
        }
        if(capacityInput.getVisibility() != View.GONE){
            Log.i("FormData","capacity type: " +capacityInput.getText().toString().trim());
        }



// Firebase references
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

// Get input data
        String category = selectedCategory;
        String description = descriptionInput.getText().toString();
        String title = titleInput.getText().toString();
        String price = priceInput.getText().toString();
        String quantity = quantityInput.getText().toString();
        String province = provinceSpinner.getSelectedItemId() != 0 ? provinceSpinner.getSelectedItem().toString() : "";
// String district = districtSpinner.getSelectedItemId() != 0 ? districtSpinner.getSelectedItem().toString() : "";
        String pickupLocationlat = pickupLat;
        String pickupLocationlang =  pickupLang;
        String pickupDate = (calendarView.getVisibility() != View.GONE) ? selectedDate : "";
        String paymentMethod = (paymentRadioGroup.getVisibility() != View.GONE) ?
                ((RadioButton)findViewById(payselectedGenderId)).getText().toString() : "";
        String vehicleType = (vehicleTypeSpinner.getVisibility() != View.GONE) ?
                vehicleTypeSpinner.getText().toString().trim() : "";
        String capacity = (capacityInput.getVisibility() != View.GONE) ?
                capacityInput.getText().toString().trim() : "";

        final Boolean[] Active_State = new Boolean[1];
        db.collection("user").document(currentUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                 Active_State[0] = documentSnapshot.getBoolean("verified");
                }
            }
        });
// Store image in Firebase Storage
        if (imageUri != null) {
            StorageReference imageRef = storageRef.child("product_images/" + System.currentTimeMillis() + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                            // Store form data in Firestore
                            Map<String, Object> productData = new HashMap<>();
                            productData.put("category", category);
                            productData.put("description", description);
                            productData.put("title", title);
                            productData.put("price", price);
                            productData.put("quantity", quantity);
                            productData.put("province", province);
                            productData.put("pickupLocationlat", pickupLocationlat);
                            productData.put("pickupLocationlang", pickupLocationlang);
                            productData.put("pickupDate", pickupDate);
                            productData.put("paymentMethod", paymentMethod);
                            productData.put("vehicleType", vehicleType);
                            productData.put("capacity", capacity);
                            productData.put("seller", currentUser);
//                            productData.put("verified",Active_State);
                            productData.put("imageUrl", uri.toString()); // Store Image URLgg
//
                            db.collection("products").add(productData)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.i("Firestore", "Product added with ID: " + documentReference.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error adding product", e);
                                    });

                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Storage", "Image upload failed", e);
                    });
        } else {
            Log.e("ImageUpload", "No image selected!");
        }


        SharedPreferences.Editor editor = s.edit();
        editor.putString("Title",null);
        editor.putString("Description",null);
        editor.apply();

    }
}
class AraaAdapter extends ArrayAdapter<CatItems> {

    List<CatItems> brandList;
    int layout;

    public AraaAdapter(@NonNull Context context, int resource, @NonNull List<CatItems> objects) {
        super(context, resource, objects);
        brandList = objects;
        layout=resource;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.dropdown_cat_layout_item,parent,false);

        TextView id = view.findViewById(R.id.textView5);
        TextView name = view.findViewById(R.id.textView6);
        ImageView img_view = view.findViewById(R.id.imageView11);

        CatItems brand= brandList.get(position);
        img_view.setImageResource(brand.getCat_img());
        name.setText(brand.getName());
        id.setText(brand.getDes());

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout,parent,false);

        TextView id = view.findViewById(R.id.textView2);

        CatItems brand= brandList.get(position);
        id.setText(brand.getName());
        return view;
    }

}