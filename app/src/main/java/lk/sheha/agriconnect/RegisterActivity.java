package lk.sheha.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import lk.sheha.agriconnect.model.SuccessDialog;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText usernameInput, fnameInput, lnameInput, emailInput, pwInput, confirmPwInput, mobInput, addL1Input, distric, provinceInput;
    private RadioGroup genderRadioGroup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String[] provinces = {
            "Select Province", // Default option
            "Western",
            "Central",
            "Southern",
            "Northern",
            "Eastern",
            "North Western",
            "North Central",
            "Uva",
            "Sabaragamuwa"
    };
    private Spinner provinceSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        provinceSpinner = findViewById(R.id.provinceSpinner);

        // Create an ArrayAdapter using the provinces array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                provinces
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        provinceSpinner.setAdapter(adapter);

        usernameInput = findViewById(R.id.usernameinput);
        fnameInput = findViewById(R.id.fnmaeinput);
        lnameInput = findViewById(R.id.lnameinput);
        emailInput = findViewById(R.id.emailInput);
        pwInput = findViewById(R.id.pwInput);
        confirmPwInput = findViewById(R.id.comfirmpwInput);
        mobInput = findViewById(R.id.mobinput);
        addL1Input = findViewById(R.id.addl1input);
        distric = findViewById(R.id.districinput);
//        provinceInput = findViewById(R.id.provinceinput);
        genderRadioGroup = findViewById(R.id.radiogroup);


        findViewById(R.id.submite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });

    }
    private void showSuccessDialog() {
        SuccessDialog successDialog = new SuccessDialog(RegisterActivity.this);
        successDialog.setCancelable(false); // Prevent dismissing by tapping outside
        successDialog.show();
    }
    private void registerUser() {

        String province = provinceSpinner.getSelectedItem().toString();

        // Log selected province
        Log.i("RegisterActivity", "Selected Province: " + province);

        // Validate province selection
        if (province.equals("Select Province")) {
            Toast.makeText(this, "Please select a province", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get input values
        String username = usernameInput.getText().toString().trim();
        String fname = fnameInput.getText().toString().trim();
        String lname = lnameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = pwInput.getText().toString().trim();
        String confirmPassword = confirmPwInput.getText().toString().trim();
        String mobile = mobInput.getText().toString().trim();
        String addressL1 = addL1Input.getText().toString().trim();
        String districs = distric.getText().toString().trim();
//        String province = provinceInput.getText().toString().trim();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender != null ? selectedGender.getText().toString() : "";

        Log.i("hellow", "Username: " + username);
        Log.i("hellow", "First Name: " + fname);
        Log.i("hellow", "Last Name: " + lname);
        Log.i("hellow", "Email: " + email);
        Log.i("hellow", "Password: " + password);
        Log.i("hellow", "Confirm Password: " + confirmPassword);
        Log.i("hellow", "Mobile: " + mobile);
        Log.i("hellow", "Address Line 1: " + addressL1);
        Log.i("hellow", "Address Line 2: " + districs);
        Log.i("hellow", "Province: " + province);
        Log.i("hellow", "Gender: " + gender);

        // Validate inputs
        if (username.isEmpty()) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return;
        }
        if (fname.isEmpty()) {
            fnameInput.setError("First name is required");
            fnameInput.requestFocus();
            return;
        }
        if (lname.isEmpty()) {
            lnameInput.setError("Last name is required");
            lnameInput.requestFocus();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid email is required");
            emailInput.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            pwInput.setError("Password must be at least 6 characters");
            pwInput.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPwInput.setError("Passwords do not match");
            confirmPwInput.requestFocus();
            return;
        }
        if (mobile.isEmpty() || mobile.length() != 10) {
            mobInput.setError("Valid mobile number is required");
            mobInput.requestFocus();
            return;
        }
        if (addressL1.isEmpty()) {
            addL1Input.setError("Address line 1 is required");
            addL1Input.requestFocus();
            return;
        }
        if (province.isEmpty()) {
            provinceInput.setError("Province is required");
            provinceInput.requestFocus();
            return;
        }
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        saveUserData(password,username, fname, lname, email, mobile, addressL1, districs, province, gender);

    }

    private void saveUserData(String pw,String username, String fname, String lname, String email, String mobile, String addressL1, String distric, String province, String gender) {
        // Create a user data map
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("firstName", fname);
        user.put("lastName", lname);
        user.put("email", email);
        user.put("mobile", mobile);
        user.put("addressLine1", addressL1);
        user.put("addressLine2", distric);
        user.put("province", province);
        user.put("gender", gender);
        user.put("pw",pw);
        user.put("iscpletedprofile","false");
        user.put("verified",false);


        // Save data to Firestore
        db.collection("user").document(email)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            showSuccessDialog();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
