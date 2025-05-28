package lk.sheha.agriconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.sheha.agriconnect.model.NotifiItem;
import lk.sheha.agriconnect.model.PaymentitemList;
import lk.sheha.agriconnect.model.ReqestItem;

public class Inboxdetails extends AppCompatActivity {

    private TextView fromuser,p_title,p_hr,tot_pay;
    private ImageView p_image;
    private ReqestItem req_items;
    private Button paybtn;
    private String PayStatus ;
    private ImageView imageView12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inboxdetails);

        req_items = (ReqestItem) getIntent().getSerializableExtra("inb_data");

        fromuser = findViewById(R.id.textView61);
        p_title = findViewById(R.id.textView63);
        p_hr = findViewById(R.id.textView64);
        tot_pay = findViewById(R.id.textView65);
        p_image = findViewById(R.id.imageView7);
        paybtn = findViewById(R.id.pay_For_driver);
        imageView12 = findViewById(R.id.imageView12);
        fromuser.setText(req_items.getFromuser());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").document(req_items.getProduct()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Glide.with(Inboxdetails.this).load(document.getString("imageUrl")).into(p_image);
                        p_title.setText(document.getString("title"));
                        p_hr.setText(req_items.getTotqty());
                        tot_pay.setText(req_items.getAmount());
                        if(req_items.getPaymentStatus().equals("pending")){
                            Log.i("hellows","awa");
                            paybtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PaymentitemList plist = new PaymentitemList(req_items.getFromuser(),document.getString("title"),req_items.getTotqty(),req_items.getAmount(),document.getString("imageUrl"),"isDriver");
                                    Intent i = new Intent(Inboxdetails.this,PaymentActivity.class);
                                    i.putExtra("seller",plist);
                                    startActivity(i);
                                }
                            });
                        }else {
                            paybtn.setEnabled(false);
                            imageView12.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

    }
}