package lk.sheha.agriconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    private ImageView profileImage;
    private TextView changeProfilePic;
    private EditText etUsername, etEmail, etAddress, etPhone;
    private Button btnSaveProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String Log_user_email,log_user_username;
    private StorageReference storageReference;

    private Uri imageUri;
    private String userEmail = "testuser@gmail.com"; // Replace this with dynamic user email
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = UpdateProfile.this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");

        if(!u.isEmpty()){
            userEmail = u;
        } else if(user != null) {
            userEmail = user.getEmail();
        }


        // Initialize UI Components
        profileImage = findViewById(R.id.profileImage);
        changeProfilePic = findViewById(R.id.changeProfilePic);
        etUsername = findViewById(R.id.etUsernameInput);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddressInput);
        etPhone = findViewById(R.id.etPhoneInput);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Set email field as non-editable
        etEmail.setEnabled(false);

        // Firebase References
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Load Existing Data
        loadUserData();

        // Select Image from Gallery
        changeProfilePic.setOnClickListener(view -> openFileChooser());

        // Save Profile Button
        btnSaveProfile.setOnClickListener(view -> saveProfileData());

    }

    private void loadUserData() {
        DocumentReference docRef = db.collection("user").document(userEmail);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    if (document.contains("username")) {
                        etUsername.setText(document.getString("username"));
                    } else {
                        etUsername.setText(""); // Default empty if not available
                    }

                    if (document.contains("email")) {
                        etEmail.setText(document.getString("email"));
                    } else {
                        etEmail.setText("");
                    }

                    if (document.contains("addressLine1")) {
                        etAddress.setText(document.getString("addressLine1"));
                    } else {
                        etAddress.setText("");
                    }

                    if (document.contains("mobile")) {
                        etPhone.setText(document.getString("mobile"));
                    } else {
                        etPhone.setText("");
                    }


                    if (document.contains("profileImage")) {
                        String imageUrl = document.getString("profileImage");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this).load(imageUrl).into(profileImage);
                        }
                    }

                } else {
                    Toast.makeText(this, "No profile data found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfileData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", etUsername.getText().toString());
        userMap.put("email", etEmail.getText().toString());
        userMap.put("address", etAddress.getText().toString());
        userMap.put("phone", etPhone.getText().toString());

        db.collection("user").document(userEmail).update(userMap)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Update Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

        if (imageUri != null) {
            uploadImage();
        }
    }

    private void uploadImage() {
        StorageReference fileRef = storageReference.child(userEmail + ".jpg");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            fileRef.putBytes(data).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        db.collection("user").document(userEmail)
                                .update("profileImage", uri.toString());
                        Toast.makeText(this, "Profile Image Updated!", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(this, "Image Upload Failed!", Toast.LENGTH_SHORT).show()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

}