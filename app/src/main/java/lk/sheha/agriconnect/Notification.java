package lk.sheha.agriconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import lk.sheha.agriconnect.model.NotifiItem;
import lk.sheha.agriconnect.model.NotificationItemAdapter;

public class Notification extends Fragment {

    private  String Log_user_email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_notification, container, false);

        RecyclerView recyclerView = rootview.findViewById(R.id.notificationrecycler);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");

        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<NotifiItem> notifidata = new ArrayList<>();

        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 10;
                outRect.bottom = 10;
            }
        };
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        NotificationItemAdapter adapter = new NotificationItemAdapter(requireContext(),notifidata);
        recyclerView.setAdapter(adapter);

        db.collection("deliver_requests")
                .whereEqualTo("to", Log_user_email)
                .whereEqualTo("status","pending")//gg
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.i("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        notifidata.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String Hours = document.getString("Hours");
                            String Product_Id = document.getString("Product_Id");
                            String Rent_Date = document.getString("Rent_Date");
                            String description = document.getString("description");
                            String droplang = document.getString("droplang");
                            String droplat = document.getString("droplat");
                            String from = document.getString("from");
                            String mob = document.getString("mob");
                            String status = document.getString("status");
                            String to = document.getString("to");

                            NotifiItem notification = new NotifiItem(
                                    Hours,Product_Id,Rent_Date,description,droplang,droplat,from,mob,status,to
                            );

                            notifidata.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        return rootview;
    }

}