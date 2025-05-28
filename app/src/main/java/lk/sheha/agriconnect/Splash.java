package lk.sheha.agriconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {


   private Handler handler = new Handler();

    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);


        runnable = new Runnable(){
            @Override
            public void run() {

                SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                boolean isOnboardingComplete = preferences.getBoolean("onboarding_complete", false);

                if (isOnboardingComplete) {

                    boolean isLogedin_google = preferences.getBoolean("is_googlelod",false);
                    boolean islogedin_namual = preferences.getBoolean("is_manuallod",false);

                    Log.i("hellow",preferences.getAll().toString());
//                    islogedin_namual
                    if(isLogedin_google){

                        startActivity(new Intent(Splash.this, UserHome.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
//                        isLogedin_google
                    } else if (islogedin_namual) {
                        startActivity(new Intent(Splash.this, UserHome.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }else {
                        startActivity(new Intent(Splash.this, MainLogin.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }

                } else {
                    startActivity(new Intent(Splash.this, OnboardActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                finish();
            }
        };
        handler.postDelayed(runnable,1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}