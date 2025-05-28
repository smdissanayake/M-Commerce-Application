package lk.sheha.agriconnect.model;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import lk.sheha.agriconnect.R;
import lk.sheha.agriconnect.SingleViewActivity;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

Context context;
List<Product> productlist;

    public ProductAdapter(Context context, List<Product> productlist) {
        this.context = context;
        this.productlist = productlist;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product =productlist.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(product.getSeller()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot docs = task.getResult();
                    if(docs.getBoolean("verified")){
                        holder.v_layout.setVisibility(View.VISIBLE);
                    }else {
                        holder.v_layout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });


        Log.i("hellow",product.getVerified().toString());
        holder.title.setText(product.getTitle());
        holder.avilqty.setText(product.getQuantity());
        holder.price.setText(product.getPrice());

        if(product.getCategory().equals("Rent Vehicle")){
            holder.qtytype.setText("Per hour");
        }else {
            holder.qtytype.setText("Per Kg");
        }
        int qty = Integer.parseInt(product.getQuantity());
        if(qty<0){
            holder.availableTxt.setText("Out of Stock");
            holder.availableTxt.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red));
        }else {
            holder.availableTxt.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_green));
        }
        if(product.getCategory().equals("Rent Vehicle")){
            holder.qtytype.setText("Per hour");
        }else {
            holder.qtytype.setText("Per Kg");
        }

        if(product.getCategory().equals("Rent Vehicle")){
            holder.button_send_req.setVisibility(View.VISIBLE);
            holder.button_pre_order.setVisibility(View.GONE);
            holder.button_buy_now.setVisibility(View.GONE);
        } else if (product.getCategory().equals("Now sell")) {
            holder.button_buy_now.setVisibility(View.VISIBLE);
            holder.button_send_req.setVisibility(View.GONE);
            holder.button_pre_order.setVisibility(View.GONE);

        } else if (product.getCategory().equals("Pre Order")) {
         holder.button_pre_order.setVisibility(View.VISIBLE);
         holder.button_buy_now.setVisibility(View.GONE);
         holder.button_send_req.setVisibility(View.GONE);

        }


//        Glide.with(context).load(product.getImageUrl()).into(holder.imgview);
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.place_holder) // Image shown while loading
                .error(R.drawable.place_holder) // Image shown on error
                .into(holder.imgview);


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleViewActivity.class);
            intent.putExtra("product", product); // Pass the entire object
//            context.startActivity(intent);

//                Intent intent = new Intent(context, SingleViewActivity.class);
//                intent.putExtra("p_id",String.valueOf(position));
//                intent.putExtra("p_name",String.valueOf(product.getTitle()));
//                intent.putExtra("p_img",String.valueOf(product.getImageUrl()));
                ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left);
                context.startActivity(intent, options.toBundle());

        });
    }


    @Override
    public int getItemCount() {
        return productlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgview;
        TextView tv,title,avilqty,price,qtytype,availableTxt;
        Button button_buy_now,button_pre_order,button_send_req;
        LinearLayout v_layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgview= itemView.findViewById(R.id.imageView5);
            tv= itemView.findViewById(R.id.textView19);
            title= itemView.findViewById(R.id.textView20);
            avilqty= itemView.findViewById(R.id.textView40);
            price= itemView.findViewById(R.id.textView23);
            qtytype= itemView.findViewById(R.id.textView22);
            availableTxt= itemView.findViewById(R.id.textView21);
            v_layout= itemView.findViewById(R.id.veify_container);

            button_buy_now= itemView.findViewById(R.id.button_buy_now);
            button_pre_order= itemView.findViewById(R.id.button_pre_order);
            button_send_req = itemView.findViewById(R.id.button_send_req);

        }
    }
}
