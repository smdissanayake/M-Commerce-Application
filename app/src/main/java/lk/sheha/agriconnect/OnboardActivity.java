package lk.sheha.agriconnect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import lk.sheha.agriconnect.model.IntroAdapter;
import lk.sheha.agriconnect.model.ScreenItem;

public class  OnboardActivity extends AppCompatActivity {
    IntroAdapter introAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboard);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("onboarding_complete", true);
        editor.apply();

        ArrayList<ScreenItem> mlist = new ArrayList<>();
        mlist.add(new ScreenItem("For Farmers",R.raw.vvd1,"Farmers Can sell own Goods on online marketplace"));
        mlist.add(new ScreenItem("Marketplace",R.raw.vvd2,"This application Provides to online marketplace"));
        mlist.add(new ScreenItem("Transport",R.raw.vvd3,"You can rent a supplier vehical Or You can Rent Your Own Vehical"));

        ViewPager screen2 = findViewById(R.id.pagevi);
        introAdapter = new IntroAdapter(this,screen2,mlist);
        screen2.setAdapter(introAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}