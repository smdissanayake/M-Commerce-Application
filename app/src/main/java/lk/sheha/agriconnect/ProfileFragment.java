package lk.sheha.agriconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String Log_user_email,log_user_username;

    private  TextView textView33;
    private ImageView imageView10;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootv =  inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences preferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String u = preferences.getString("email","");
        ImageView profileImage =rootv.findViewById(R.id.imageView8);
        if(!u.isEmpty()){
            Log_user_email = u;
        } else if(user != null) {
            Log_user_email = user.getEmail();
            log_user_username = user.getDisplayName();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        imageView10 = rootv.findViewById(R.id.imageView10);
        db.collection("user").document(Log_user_email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains("profileImage")) {
                            String imageUrl = document.getString("profileImage");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(rootv.getContext()).load(imageUrl).into(profileImage);
                            }
                        }

                        if(document.contains("verified")){
                            boolean imageUrl = document.getBoolean("verified");
                            if (imageUrl) {
                                imageView10.setVisibility(View.VISIBLE);
//                                Glide.with(rootv.getContext()).load(imageUrl).into(profileImage);
                            }
                        }
                    }
                }
            }
        });

        textView33 = rootv.findViewById(R.id.textView33);
        textView33.setText(log_user_username);

//        db.collection("user").document(Log_user_email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()){
//                    DocumentSnapshot dc = task.getResult();
//                    String verify = dc.getString("verified");
//                    if(verify.equals("true")){
//                        imageView10.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        });
        Log.i("hellowuswe",Log_user_email);

        TextView addProduct = rootv.findViewById(R.id.textView38);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));
            }
        });

        TextView myreq = rootv.findViewById(R.id.textView39);
        myreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyrequestActivity.class));
            }
        });

        TextView inbox = rootv.findViewById(R.id.textView37);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), InboxRequest.class));
            }
        });

        TextView my_Orders = rootv.findViewById(R.id.textView35);
        my_Orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyProductsActivity.class));
            }
        });

        TextView gg = rootv.findViewById(R.id.activate_profilr);
        gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), verifyprofile.class));
            }
        });

        TextView update = rootv.findViewById(R.id.textView36);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UpdateProfile.class));
            }
        });

        return rootv;
    }
}