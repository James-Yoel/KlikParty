package umn.ac.id.uas_mobileandroid_2021_a3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private Context mContext;
    private Activity mActivity;
    private List<History> mHistory;
    private NumberFormat numberFormat;

    public HistoryAdapter(Context mContext, Activity mActivity, List<History> mHistory){
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mHistory = mHistory;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_layout, parent, false);
        numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        History history = mHistory.get(position);
        String type = "Book " + history.getBookType();
        holder.bookType.setText(type);
        holder.orderMade.append(history.getOrderMade());
        holder.bookDate.append(history.getBookDate());
        holder.bookTime.append(history.getBookTime());
        holder.orderId.append(history.getOrderId());
        holder.bookLocation.append(history.getBookLocation());
        holder.bookPrice.append(numberFormat.format(history.getBookPrice()));
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView bookType, orderMade, bookDate, bookTime, bookLocation, orderId, bookPrice;
        public CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            bookType = itemView.findViewById(R.id.bookType);
            orderMade = itemView.findViewById(R.id.orderMade);
            bookDate = itemView.findViewById(R.id.bookDate);
            bookTime = itemView.findViewById(R.id.bookTime);
            bookLocation = itemView.findViewById(R.id.bookLocation);
            orderId = itemView.findViewById(R.id.orderId);
            bookPrice = itemView.findViewById(R.id.bookPrice);
            container = itemView.findViewById(R.id.card_viewHistory);
        }

        @Override
        public void onClick(View v) {
            int mPosition = getLayoutPosition();
            History history = mHistory.get(mPosition);
            Intent intent = new Intent(mActivity, HistoryDetail.class);
            intent.putExtra("Order", history.getOrderId());
            mActivity.startActivity(intent);
        }
    }
}
