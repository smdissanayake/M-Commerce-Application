package lk.sheha.agriconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHome extends AppCompatActivity {

    private String Log_user_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");

        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
        }


        BottomNavigationView bottomNavigationView=findViewById(R.id.botmNavigation);
        bottomNavigationView.setSelectedItemId(R.id.Home);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.Home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.Profile) {
                    selectedFragment = new ProfileFragment();
                }else if (item.getItemId() == R.id.Notification) {
                    selectedFragment = new Notification();
                }else if (item.getItemId() == R.id.Search) {
                    selectedFragment = new Search();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right
                            )
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            android.R.anim.slide_in_left, // Pop enter animation
                            android.R.anim.slide_out_right // Pop exit animation
                    )
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("deliver_requests")
                .whereEqualTo("to", Log_user_email)
                .whereEqualTo("status", "pending")  // Count only pending notifications
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.i("Firestore", "Failed to fetch notifications", error);
                        return;
                    }
                    int count = value != null ? value.size() : 0;
                    BottomNavigationView bottomNav = this.findViewById(R.id.botmNavigation);
                    BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.Notification);
                    badge.setVisible(count > 0);
                    badge.setNumber(count);
                });
    }

}