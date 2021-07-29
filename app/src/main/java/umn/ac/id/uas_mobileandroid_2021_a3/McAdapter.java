package umn.ac.id.uas_mobileandroid_2021_a3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.number.NumberFormatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class McAdapter extends RecyclerView.Adapter<McAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<MC> mMC;
    private List<String> chosen;
    NumberFormat numberFormat;

    public McAdapter(Context mContext, Activity mActivity, List<MC> mMC, List<String> chosen){
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mMC = mMC;
        this.chosen = chosen;
    }

    @NonNull
    @Override
    public McAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mc_layout, parent, false);
        numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return new McAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull McAdapter.ViewHolder holder, int position) {
        boolean b = false;
        MC mc = mMC.get(position);
        holder.mcName.setText(mc.getName());
        String price = numberFormat.format(mc.getPrice()) + " / hour";
        holder.mcPrice.setText(price);
        if(mc.getImage().equals("Default")){
            Picasso.get().load(R.drawable.logo_klikparty).into(holder.mcImage);
        }
        else{
            Picasso.get().load(mc.getImage()).into(holder.mcImage);
        }
        if(chosen.isEmpty()){
            holder.checked.setVisibility(View.GONE);
        }
        else{
            for(int i = 0; i < chosen.size(); i++){
                if(Integer.parseInt(chosen.get(i)) == position){
                    holder.checked.setVisibility(View.VISIBLE);
                    break;
                }
                else {
                    holder.checked.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMC.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context context;
        public TextView mcName, mcPrice;
        public ImageView mcImage, checked;
        public CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mcName = itemView.findViewById(R.id.mcName);
            mcPrice = itemView.findViewById(R.id.mcPrice);
            mcImage = itemView.findViewById(R.id.mcImage);
            container = itemView.findViewById(R.id.card_view);
            checked = itemView.findViewById(R.id.checked);
        }

        @Override
        public void onClick(View v) {
            boolean check = false;
            int mPosition = getLayoutPosition();
            MC mc = mMC.get(mPosition);
            Intent intent = new Intent(mActivity, DetailMc.class);
            intent.putExtra("MC", mc.getName());
            intent.putExtra("Pos", mPosition);
            for(int i = 0; i < chosen.size(); i++){
                if (Integer.parseInt(chosen.get(i)) == mPosition){
                    check = true;
                    intent.putExtra("Type", 2);
                    mActivity.startActivityForResult(intent, 2);
                    break;
                }
            }
            if(!check){
                if(chosen.size() >= 2){
                    new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("You can only booked MC in total of 2")
                            .show();
                }
                else{
                    intent.putExtra("Type", 1);
                    mActivity.startActivityForResult(intent, 1);
                }
            }
        }
    }
}
