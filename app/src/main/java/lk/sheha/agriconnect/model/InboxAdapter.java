package lk.sheha.agriconnect.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.sheha.agriconnect.Inboxdetails;
import lk.sheha.agriconnect.R;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private List<ReqestItem> list;
    private Context context;

    public InboxAdapter(List<ReqestItem> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_request_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxAdapter.ViewHolder holder, int position) {
        ReqestItem itemsData = list.get(position);
        Log.i("hellows",itemsData.getAmount());
        holder.textView59.setText(itemsData.getFromuser());
        holder.textView53.setText(itemsData.getPaymentStatus());
        holder.textView54.setText("Rs "+itemsData.getAmount());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, Inboxdetails.class);
                i.putExtra("inb_data",itemsData);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
       TextView textView59,textView53,textView54;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView59 = itemView.findViewById(R.id.textView59);
            textView53 = itemView.findViewById(R.id.textView53);
            textView54 = itemView.findViewById(R.id.textView54);

        }
    }
}
