package lk.sheha.agriconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SuccsessAnimation extends AppCompatActivity {
    private Handler handler = new Handler();

    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_succsess_animation);

        runnable = new Runnable(){
            @Override
            public void run() {
                    startActivity(new Intent(SuccsessAnimation.this, UserHome.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        };
        handler.postDelayed(runnable,3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}