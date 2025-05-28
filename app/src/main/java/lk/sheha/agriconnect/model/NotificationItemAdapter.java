package lk.sheha.agriconnect.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import lk.sheha.agriconnect.R;
import lk.sheha.agriconnect.RequestDetails;
import lk.sheha.agriconnect.SingleViewActivity;

public class NotificationItemAdapter extends RecyclerView.Adapter<NotificationItemAdapter.ViewHolder> {

    private Context context;
    private List<NotifiItem> notifi_list;


    public NotificationItemAdapter(Context context, List<NotifiItem> notifi_list) {
        this.context = context;
        this.notifi_list = notifi_list;
    }

//    public NotificationItemAdapter(Context context, List notifi_list) {
//        this.context = context;
//        this.notifi_list = notifi_list;
//    }

    @NonNull
    @Override
    public NotificationItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notifications,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationItemAdapter.ViewHolder holder, int position) {
        NotifiItem notifiItem = notifi_list.get(position);
        holder.textView29.setText("Delivery Request");
        holder.textView28.setText("gfg");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RequestDetails.class);
                intent.putExtra("notifi_data",notifiItem);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifi_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView29,textView28;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView29 = itemView.findViewById(R.id.textView29);
            textView28 =itemView.findViewById(R.id.textView28);
        }
    }
}
