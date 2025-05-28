package lk.sheha.agriconnect;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lk.sheha.agriconnect.model.CartItem;
import lk.sheha.agriconnect.model.CartItemAdapter;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.SubtotalUpdateListener {

    private TextView subtotalText;
    private List<CartItem> cartItems;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        subtotalText = findViewById(R.id.textView15);

        recyclerView = findViewById(R.id.cartview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = 10; // Horizontal margin
                outRect.right = 10;
                outRect.top = 20;
                outRect.bottom = 20;
            }
        };
        recyclerView.addItemDecoration(decoration);
        cartItems = new ArrayList<>();
        cartItems.add(new CartItem("sdfsg sgfsdfg dfgd fdge","100","Badulla","150",R.drawable.p1));
        cartItems.add(new CartItem("sdfsg sgfsdfg dfgd fdge","100","Badulla","150",R.drawable.p1));
        cartItems.add(new CartItem("sdfsg sgfsdfg dfgd fdge","100","Badulla","150",R.drawable.p1));
        cartItems.add(new CartItem("vee","100","Badulla","150",R.drawable.p2));
        cartItems.add(new CartItem("vee","100","Badulla","150",R.drawable.p2));
        cartItems.add(new CartItem("vee","100","Badulla","150",R.drawable.p2));


        CartItemAdapter cartItemAdapter = new CartItemAdapter(CartActivity.this,cartItems,this);
        recyclerView.setAdapter(cartItemAdapter);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onSubtotalUpdated(int subtotal) {
        subtotalText.setText("Rs " + subtotal);
    }
    private void updateSubtotal() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += Integer.parseInt(item.getQty()) * Integer.parseInt(item.getPrice());
        }
        subtotalText.setText("Rs " + total);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}

class swiper extends ItemTouchHelper.Callback{

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return 0;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }
}