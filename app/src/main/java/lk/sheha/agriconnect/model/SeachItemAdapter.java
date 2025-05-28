package lk.sheha.agriconnect.model;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.sheha.agriconnect.R;
import lk.sheha.agriconnect.SingleViewActivity;

public class SeachItemAdapter extends RecyclerView.Adapter<SeachItemAdapter.ViewHolder> {
    private List<Product> prodfuctList;
    private Context context;

    public SeachItemAdapter(Context context,List<Product> productList ) {
        this.prodfuctList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public SeachItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_search_result,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeachItemAdapter.ViewHolder holder, int position) {
    Product product = prodfuctList.get(position);

    holder.title.setText(product.getTitle());
        if(product.getCategory().equals("Rent Vehicle")){
            holder.price.setText(product.getPrice() +" /Per hour");
            holder.qty.setText("Hourly Rate");
        }else {
            holder.price.setText(product.getPrice()+" /Per Kg");
            holder.qty.setText(product.getQuantity());
        }

        if(product.getCategory().equals("Rent Vehicle")){
            holder.rent.setVisibility(View.VISIBLE);
            holder.buyNow.setVisibility(View.GONE);
            holder.preOrder.setVisibility(View.GONE);
        } else if (product.getCategory().equals("Now sell")) {
            holder.buyNow.setVisibility(View.VISIBLE);
            holder.rent.setVisibility(View.GONE);
            holder.preOrder.setVisibility(View.GONE);
        } else if (product.getCategory().equals("Pre Order")) {
            holder.preOrder.setVisibility(View.VISIBLE);
            holder.buyNow.setVisibility(View.GONE);
            holder.rent.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.place_holder) // Image shown while loading
                .error(R.drawable.place_holder) // Image shown on error
                .into(holder.ingView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleViewActivity.class);
            intent.putExtra("product", product); // Pass the entire object
//            Intent intent = new Intent(context, SingleViewActivity.class);
//            intent.putExtra("p_id",String.valueOf(position));
            ActivityOptions options = ActivityOptions.makeCustomAnimation(
                    context, R.anim.slide_in_right, R.anim.slide_out_left);
            context.startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return prodfuctList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title,price,qty,buyNow,preOrder,rent;
        private ImageView ingView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_txt);
            price = itemView.findViewById(R.id.price_txt);
            qty = itemView.findViewById(R.id.qty_txt);
            buyNow = itemView.findViewById(R.id.buy_now_txt);
            rent = itemView.findViewById(R.id.rent_txt);
            preOrder = itemView.findViewById(R.id.pre_order_txt);
            ingView = itemView.findViewById(R.id.imageView4);
        }
    }
}
