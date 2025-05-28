package lk.sheha.agriconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.sheha.agriconnect.model.ReqestItem;
import lk.sheha.agriconnect.model.InboxAdapter;

public class InboxRequest extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String Log_user_email;
    private  ArrayList<ReqestItem> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inbox);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");

        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
        }
        list = new ArrayList<>();
        RecyclerView view = findViewById(R.id.inboxrecycler);
        view.setLayoutManager(new LinearLayoutManager(InboxRequest.this));
        InboxAdapter adapter = new InboxAdapter(list, InboxRequest.this);
        view.setAdapter(adapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("myRequest")
                .whereEqualTo("hireowner", Log_user_email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            QuerySnapshot doc = task.getResult();
                            list.clear();
                            for (DocumentSnapshot docd : doc.getDocuments()) {
                                if (docd.exists()) { // Check if document exists
                                    Log.i("Firestore", "Document found: " + docd.getString("amount"));
                                    list.add(new ReqestItem( // Corrected class name
                                            docd.getString("Fromuser"),
                                            docd.getString("hireowner"),
                                            docd.getString("requestId"),
                                            docd.getString("paymentStatus"),
                                            docd.getString("amount"),
                                            docd.getString("seen"),
                                            docd.getString("product"),
                                            docd.getString("time"),
                                            docd.getString("Hours")

                                    ));
                                }
                            }
                            adapter.notifyDataSetChanged(); // Update UI
                        } else {
                            Log.e("Firestore", "Error fetching data", task.getException());
                        }
                    }
                });





    }
}