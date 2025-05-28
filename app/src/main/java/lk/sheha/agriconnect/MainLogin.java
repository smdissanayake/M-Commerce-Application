package lk.sheha.agriconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;
import android.database.Cursor;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import lk.sheha.agriconnect.Helper.UserDatabaseHelper;
import lk.sheha.agriconnect.model.IsValidate;

public class MainLogin extends AppCompatActivity {

    private TextInputLayout emailLayout,pwLayout;
    private  TextInputEditText emailInput;
    private  TextView textView,register;

    private EditText Email,Pw;
    private Button Submitebtn,btnGoogleSignIn,btnSignOut;
    private UserDatabaseHelper dbhelper;

//    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_login);

        emailInput = findViewById(R.id.emailInput);
//        emailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(emailLayout.getError() != null){
//                emailLayout.setError(null);
//                }
//            }
//        });

        Email = findViewById(R.id.emailInput);
        Pw = findViewById(R.id.pwInput);
        emailLayout = findViewById(R.id.emailLayout);
        pwLayout = findViewById(R.id.pwlayout);

        String stremail = Email.getText().toString();
        String strpw = Pw.getText().toString();


        textView =  findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLogin.this, RequestResetPw.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        final String[] emailgoogle = {""};
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                emailgoogle[0] = user.getEmail();
            }
        });

        Submitebtn = findViewById(R.id.button_buy_now);
        Submitebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Email = findViewById(R.id.emailInput);
                Pw = findViewById(R.id.pwInput);
                
                String stremail = Email.getText().toString();
                String strpw = Pw.getText().toString();

                emailLayout.setError(null);
                pwLayout.setError(null);

                if(stremail.isEmpty()){

                    emailLayout.setError("Enter Your Email");

                } else if (!IsValidate.isValidEmail(stremail)) {

                    emailLayout.setError("Invalid email address");

                }else if (strpw.isEmpty()) {

                    pwLayout.setError("Place Enter Email");

                }else {

                    emailLayout.setError(null);
                    pwLayout.setError(null);

//                    if(stremail.equals(emailgoogle[0])){
//                        Toast.makeText(MainLogin.this, "Email Already Taken", Toast.LENGTH_SHORT).show();
//                    }else {}
                        Submitebtn.setVisibility(View.INVISIBLE);
                        ProgressBar progressBar = findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.VISIBLE);


                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("user")
                                .where(
                                        Filter.and(
                                                Filter.equalTo("email",stremail),
                                                Filter.equalTo("pw",strpw)
                                        )
                                )
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        Submitebtn.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        dbhelper = new UserDatabaseHelper(MainLogin.this);
                                        SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        if (task.isSuccessful()) {
                                            if(task.getResult().isEmpty()){
                                                editor.putBoolean("is_manuallod",false);
                                                editor.putString("email",null);
                                                editor.apply();
                                                Toast.makeText(MainLogin.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                                                emailLayout.setError("Invalid");
                                                pwLayout.setError("Invalid");
                                                Log.i("hellow","empty");
                                            }else {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.i("hellow","is success Log");
                                                    editor.putBoolean("is_manuallod",true);
                                                    editor.putString("email",stremail);
                                                    editor.apply();
//                                                    dbhelper.insertUser(document.getString("username"),document.getString("email"),document.getBoolean("verified").toString(),document.getString("mobile"));
                                                    Intent intent = new Intent(MainLogin.this, UserHome.class);
                                                    startActivity(intent);
                                                    finish();
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                                }
                                            }
                                        } else {
                                            Log.i("hellow","Invalid Log");

                                        }
                                    }
                                });
                    }

                }
        });


//        login with google
        btnGoogleSignIn= findViewById(R.id.button4);
        btnSignOut = findViewById(R.id.button5);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleSignIn.setOnClickListener(v -> signIn());

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        register = findViewById(R.id.registerpg);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainLogin.this, RegisterActivity.class);
//                Intent intent = new Intent(MainLogin.this, RequestResetPw.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed!", Toast.LENGTH_SHORT).show();
                Log.i("hellow",e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("is_googlelod",true);
                        editor.apply();
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(MainLogin.this,UserHome.class));
                        finish();
                        Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
//                        updateUI(user);


                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                                if(isNewUser){
                                    HashMap<String ,Object> googleuser = new HashMap<>();
                                    googleuser.put("email",user.getEmail());
                                    googleuser.put("verified",false);
                                    Log.i("hellow","gg");

                                    UserDatabaseHelper writer = new UserDatabaseHelper(MainLogin.this);
                                    Cursor cur = writer.getUserByEmail(user.getDisplayName());
                                    if (cur.moveToNext()){
                                        writer.insertUser(user.getDisplayName(),user.getEmail(),googleuser.get("verified").toString(),"");
                                    }
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("user").document(user.getEmail())
                                            .set(googleuser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.i("hellow","gg oks");
                                                }
                                            });
                                }
                    } else {
                        Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("is_googlelod",false);
            editor.apply();
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();

            updateUI(null);
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            btnGoogleSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
        } else {
            btnGoogleSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        finishAffinity();
        return super.getOnBackInvokedDispatcher();
    }
}