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
import java.util.List;
import java.util.Locale;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<Equipment> mEquipment;
    private List<String> chosen;
    private NumberFormat numberFormat;
    private List<EqBooked> eqBooked;

    public EquipmentAdapter(Context mContext, Activity mActivity, List<Equipment> mEquipment, List<String> chosen, List<EqBooked> eqBooked){
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mEquipment = mEquipment;
        this.chosen = chosen;
        this.eqBooked = eqBooked;
    }

    @NonNull
    @Override
    public EquipmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.equipment_layout, parent, false);
        numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return new EquipmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentAdapter.ViewHolder holder, int position) {
        Equipment equipment = mEquipment.get(position);
        holder.eqName.setText(equipment.getName());
        String price = numberFormat.format(equipment.getPrice());
        holder.eqPrice.setText(price);
        if(equipment.getImage().equals("Default")){
            Picasso.get().load(R.drawable.logo_umn).into(holder.eqImage);
        }
        else{
            Picasso.get().load(equipment.getImage()).into(holder.eqImage);
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
        return mEquipment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context context;
        public TextView eqName, eqPrice;
        public ImageView eqImage, checked;
        public CardView container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            eqName = itemView.findViewById(R.id.equipName);
            eqPrice = itemView.findViewById(R.id.eqPrice);
            eqImage = itemView.findViewById(R.id.equipImage);
            container = itemView.findViewById(R.id.card_view);
            checked = itemView.findViewById(R.id.checked);
        }

        @Override
        public void onClick(View v) {
            boolean check = false;
            int mPosition = getLayoutPosition();
            Equipment equipment = mEquipment.get(mPosition);
            Intent intent = new Intent(mActivity, DetailEquipment.class);
            intent.putExtra("Equipment", equipment.getName());
            intent.putExtra("Pos", mPosition);
            for(int i = 0; i < chosen.size(); i++){
                if (Integer.parseInt(chosen.get(i)) == mPosition){
                    check = true;
                    intent.putExtra("Type", 2);
                    intent.putExtra("Amount", String.valueOf(eqBooked.get(i).getAmount()));
                    mActivity.startActivityForResult(intent, 2);
                    break;
                }
            }
            if(!check){
                intent.putExtra("Type", 1);
                mActivity.startActivityForResult(intent, 1);
            }
        }
    }
}
