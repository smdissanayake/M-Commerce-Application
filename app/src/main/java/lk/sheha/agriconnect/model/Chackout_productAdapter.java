package lk.sheha.agriconnect.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lk.sheha.agriconnect.PaymentActivity;
import lk.sheha.agriconnect.R;

public class Chackout_productAdapter extends RecyclerView.Adapter<Chackout_productAdapter.ViewHolder> {
    List<PaymentitemList> list;
    Context context;

    public Chackout_productAdapter(List<PaymentitemList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Chackout_productAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_payment_view,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Chackout_productAdapter.ViewHolder holder, int position) {

        PaymentitemList pacti = list.get(position);

        Glide.with(context).load(pacti.getImageuri()).into(holder.imgProduct);
        holder.tvProductName.setText(pacti.getTitle());
        holder.tvProductPrice.setText(pacti.getPrice());
        holder.tvUserName.setText(pacti.getName());
        holder.tvProductWeight.setText(pacti.getQut());
//        holder.tvProductName.setText(lis);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUserName,tvProductName,tvProductWeight,tvProductPrice;
        ImageView imgProduct;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             tvUserName = itemView.findViewById(R.id.tvUserName);
             tvProductName = itemView.findViewById(R.id.tvProductName);
             tvProductWeight= itemView.findViewById(R.id.tvProductWeight);
             tvProductPrice= itemView.findViewById(R.id.tvProductPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);

        }
    }
}
