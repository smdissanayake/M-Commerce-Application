package lk.sheha.agriconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.PixelCopy;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class verifyprofile extends AppCompatActivity {

    private static final int PICK_IMAGE_FRONT = 1;
    private static final int PICK_IMAGE_BACK = 2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String Log_user_email,log_user_username;
    private static final String SERVER_URL = "https://electric-newt-verified.ngrok-free.app/agriconnect/Upload";

    private TextView responseTextView; // UI element to display response
    private OkHttpClient client = new OkHttpClient();
    private ImageView idCardFrontPreview, idCardBackPreview;
    private String frontImageBase64, backImageBase64;
    private Button Chack_status;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verifyprofile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");

        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
        }
        sendGetRequestifhas();
//        userIdInput = findViewById(R.id.userIdInput);
        idCardFrontPreview = findViewById(R.id.idCardFrontPreview);
        idCardBackPreview = findViewById(R.id.idCardBackPreview);
        Button selectFrontImage = findViewById(R.id.selectFrontImage);
        Button selectBackImage = findViewById(R.id.selectBackImage);
        Button uploadImages = findViewById(R.id.uploadImages);
        Chack_status = findViewById(R.id.Chack_status);
         pb =findViewById(R.id.progressBar3);


        selectFrontImage.setOnClickListener(v -> selectImage(PICK_IMAGE_FRONT));
        selectBackImage.setOnClickListener(v -> selectImage(PICK_IMAGE_BACK));
        uploadImages.setOnClickListener(v -> uploadImages());
        Chack_status.setOnClickListener(view -> sendGetRequest());

    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                if (requestCode == PICK_IMAGE_FRONT) {
                    idCardFrontPreview.setImageBitmap(bitmap);
                    frontImageBase64 = encodeImageToBase64(bitmap);
                } else if (requestCode == PICK_IMAGE_BACK) {
                    idCardBackPreview.setImageBitmap(bitmap);
                    backImageBase64 = encodeImageToBase64(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    private void uploadImages() {
        if (frontImageBase64 == null || backImageBase64 == null) {
            Toast.makeText(this, "Please select both images", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("useremail", Log_user_email);
                jsonObject.put("username", Log_user_email);
                jsonObject.put("frontImage", frontImageBase64);
                jsonObject.put("backImage", backImageBase64);

                String jsonInputString = jsonObject.toString();
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                String responseMessage;
                try (Scanner scanner = new Scanner(connection.getInputStream())) {
                    responseMessage = scanner.useDelimiter("\\A").next();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            Toast.makeText(verifyprofile.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                           LinearLayout l = findViewById(R.id.linearLayout7);
                           l.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(verifyprofile.this, "Upload Failed: " + responseMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } catch (IOException | JSONException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(verifyprofile.this, "Error: " , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button Chack_status = findViewById(R.id.Chack_status);
        Chack_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void sendGetRequest() {
        String SERVER_URL1 = "https://electric-newt-verified.ngrok-free.app/agriconnect/chackStatus"; // Replace with your API URL


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Append query parameters to the URL
                String queryParams = String.format("?useremail=%s&username=%s",
                        URLEncoder.encode(Log_user_email, "UTF-8"),
                        URLEncoder.encode(Log_user_email, "UTF-8"));

                URL url = new URL(SERVER_URL1 + queryParams);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");



                int responseCode = connection.getResponseCode();
                StringBuilder responseMessage = new StringBuilder();

                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseMessage.append(line);
                    }
                }

                String finalResponse = responseMessage.toString();
                runOnUiThread(() -> {
                    TextView tv = findViewById(R.id.textView32);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        if(finalResponse.equals("no")){
                            Toast.makeText(verifyprofile.this, "Pending", Toast.LENGTH_LONG).show();
                        }else {
                            tv.setVisibility(View.GONE);
                            LinearLayout container =  findViewById(R.id.verify_container);
                            container.setVisibility(View.VISIBLE);
                            Toast.makeText(verifyprofile.this, "Verified", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(verifyprofile.this, "Request Failed: " + finalResponse, Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(verifyprofile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void sendGetRequestifhas() {
        String SERVER_URL1 = "https://electric-newt-verified.ngrok-free.app/agriconnect/haveuser"; // Replace with your API URL


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Append query parameters to the URL
                String queryParams = String.format("?useremail=%s&username=%s",
                        URLEncoder.encode(Log_user_email, "UTF-8"),
                        URLEncoder.encode(Log_user_email, "UTF-8"));

                URL url = new URL(SERVER_URL1 + queryParams);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");



                int responseCode = connection.getResponseCode();
                StringBuilder responseMessage = new StringBuilder();

                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseMessage.append(line);
                    }
                }

                String finalResponse = responseMessage.toString();
                runOnUiThread(() -> {
//                    TextView tv = findViewById(R.id.textView32);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        if(finalResponse.equals("NotFound")){// no ,Verify ,Reject,NotFound
                            Toast.makeText(verifyprofile.this, "not found", Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                            LinearLayout layouts = findViewById(R.id.linearLayout7);
                            layouts.setVisibility(View.VISIBLE);

                        }else if(finalResponse.equals("no")){
                            Toast.makeText(verifyprofile.this, "Found ", Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                            TextView pending = findViewById(R.id.textView32);
                            pending.setVisibility(View.VISIBLE);
                        } else if (finalResponse.equals("Verify")) {
                            pb.setVisibility(View.GONE);
                            LinearLayout container =  findViewById(R.id.verify_container);
                            container.setVisibility(View.VISIBLE);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String,Object> umap = new HashMap<>();
                            umap.put("verified",true);
                            db.collection("user").document(Log_user_email).update(umap);
                        } else if (finalResponse.equals("Reject")) {
                            Chack_status.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(verifyprofile.this, "Request Failed: " + finalResponse, Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(verifyprofile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
