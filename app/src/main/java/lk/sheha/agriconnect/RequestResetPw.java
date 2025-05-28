package lk.sheha.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import java.util.concurrent.TimeUnit;

public class RequestResetPw extends AppCompatActivity {
    private EditText reqemailInput, otpEditText;
    private Button sendOtpButton, verifyOtpButton;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_reset_pw);

        mAuth = FirebaseAuth.getInstance();
        reqemailInput = findViewById(R.id.reqemailInput);
        sendOtpButton = findViewById(R.id.verifyCode);
        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = reqemailInput.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendVerificationCode(reqemailInput.getText().toString().trim());
                } else {
                    Toast.makeText(RequestResetPw.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        // Format the phone number with the country code
        String formattedPhoneNumber = "+94761784000";
//        String formattedPhoneNumber = "+94" + phoneNumber;

        // Configure the OTP request
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+94761784000") // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
                        .setActivity(this) // Activity for reCAPTCHA flow
                        .setCallbacks(mCallbacks) // Callbacks for OTP verification
                        .build();

        // Send the OTP
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    // Auto-retrieval or instant verification succeeded
                    Log.i("SendOtpActivity", "Verification completed: " + credential.getSmsCode());
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    // Verification failed
                    Log.e("SendOtpActivity", "Verification failed: " + e.getMessage());
                    Toast.makeText(RequestResetPw.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // OTP code sent successfully
                    Log.i("SendOtpActivity", "OTP code sent.");
                    Toast.makeText(RequestResetPw.this, "OTP code sent.", Toast.LENGTH_SHORT).show();

                    // Save the verification ID and navigate to the Verify OTP activity
                    RequestResetPw.this.verificationId = verificationId;
                    Intent intent = new Intent(RequestResetPw.this, ResetCode.class);
                    intent.putExtra("verificationId", verificationId);
                    intent.putExtra("phoneNumber", reqemailInput.getText().toString().trim());
                    startActivity(intent);
                }
            };

}