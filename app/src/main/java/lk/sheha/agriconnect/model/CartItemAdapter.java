package lk.sheha.agriconnect.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.sheha.agriconnect.R;
import lk.sheha.agriconnect.SingleViewActivity;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>{

   Context context;
    private List<CartItem> cartitemlist;
    private SubtotalUpdateListener subtotalUpdateListener;
    public CartItemAdapter(Context context, List<CartItem> cartitemlist,SubtotalUpdateListener listener) {
        this.context = context;
        this.cartitemlist = cartitemlist;
        this.subtotalUpdateListener =listener;
    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartitemlist.get(position);

        // Set product title and image
        holder.productTitl.setText(cartItem.getTitle());
        holder.cartpimg.setImageResource(cartItem.getImage_src());

        // Ensure quantity is not null or empty before setting it
        holder.SelectedQty.setText(cartItem.getQty());

        // Safely calculate total price
        try {
            int quantity = Integer.parseInt(cartItem.getQty());
            int price = Integer.parseInt(cartItem.getPrice());
            int total = quantity * price;

            holder.cartTotal.setText(String.valueOf(total)); // Set calculated total price
        } catch (NumberFormatException e) {
            holder.cartTotal.setText("Error"); // Fallback value if parsing fails
            e.printStackTrace();
        }

        // Handle the plus button click to increase quantity
        holder.plusbtn.setOnClickListener(v -> {
            try {
                int currentQuantity = Integer.parseInt(cartItem.getQty());
                int newQuantity = currentQuantity + 1;
                cartItem.setQty(String.valueOf(newQuantity));
                holder.SelectedQty.setText(String.valueOf(newQuantity)); // Update UI

                // Recalculate and update the total price
                int price = Integer.parseInt(cartItem.getPrice());
                int newTotal = newQuantity * price;
                holder.cartTotal.setText(String.valueOf(newTotal));

                notifyItemChanged(position); // Refresh RecyclerView item
                notifySubtotalUpdated();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });

        // Handle the minus button click to decrease quantity (minimum 1)
        holder.minusbtn.setOnClickListener(v -> {
            try {
                int currentQuantity = Integer.parseInt(cartItem.getQty());
                if (currentQuantity > 1) {
                    int newQuantity = currentQuantity - 1;
                    cartItem.setQty(String.valueOf(newQuantity));
                    holder.SelectedQty.setText(String.valueOf(newQuantity));

                    int price = Integer.parseInt(cartItem.getPrice());
                    int newTotal = newQuantity * price;
                    holder.cartTotal.setText(String.valueOf(newTotal));

                    notifyItemChanged(position);
                    notifySubtotalUpdated();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartitemlist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView productTitl,Quantity,Distric,SelectedQty,price,cartTotal;
        ImageView cartpimg;
        ImageView plusbtn,minusbtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitl = itemView.findViewById(R.id.cartitemtitle);
            Quantity = itemView.findViewById(R.id.cartitemqty);
            Distric = itemView.findViewById(R.id.cartitemdistric);
            SelectedQty = itemView.findViewById(R.id.selectedqty);
//            price = itemView.findViewById(R.id.cartprice);
            cartTotal = itemView.findViewById(R.id.carttotl);
            cartpimg=itemView.findViewById(R.id.cartpimg);
            plusbtn=itemView.findViewById(R.id.plusbtn);
            minusbtn=itemView.findViewById(R.id.minusbtn);

        }
    }
    // Notify CartActivity to update subtotal
    private void notifySubtotalUpdated() {
        if (subtotalUpdateListener != null) {
            int subtotal = 0;
            for (CartItem item : cartitemlist) {
                subtotal += Integer.parseInt(item.getQty()) * Integer.parseInt(item.getPrice());
            }
            subtotalUpdateListener.onSubtotalUpdated(subtotal);
        }
    }

    // Interface for Subtotal Update
    public interface SubtotalUpdateListener {
        void onSubtotalUpdated(int subtotal);
    }
}
