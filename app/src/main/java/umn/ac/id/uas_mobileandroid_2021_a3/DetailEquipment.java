package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailEquipment extends AppCompatActivity {

    TextView eqDetailName, eqDesc, eqAmount, eqSize, eqColor, eqPrice;
    EditText etAmount;
    ImageView eqImg;
    Equipment mEquipment;
    EqBooked eqBooked;
    Button finish, remove;
    NumberFormat numberFormat;
    private DatabaseReference reference;
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(DetailEquipment.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(DetailEquipment.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_detail_equipment);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Equipment Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            });
            numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            Bundle extras = getIntent().getExtras();
            String eqPick = extras.getString("Equipment");
            int type = extras.getInt("Type");
            mEquipment = new Equipment();
            eqDetailName = findViewById(R.id.eqDetailName);
            eqSize = findViewById(R.id.eqSize);
            eqColor = findViewById(R.id.eqColor);
            eqImg = findViewById(R.id.eqDetailImg);
            eqDesc = findViewById(R.id.eqDetail);
            eqAmount = findViewById(R.id.eqAmount);
            eqPrice = findViewById(R.id.eqDetailPrice);
            finish = findViewById(R.id.eqFinish);
            remove = findViewById(R.id.eqRemove);
            etAmount = findViewById(R.id.etAmount);
            if (type == 2) {
                remove.setVisibility(View.VISIBLE);
                finish.setVisibility(View.GONE);
            } else {
                finish.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
            reference = FirebaseDatabase.getInstance().getReference("Equipment").child(eqPick);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mEquipment = snapshot.getValue(Equipment.class);
                    eqDetailName.setText(mEquipment.getName());
                    eqColor.append(mEquipment.getColor());
                    eqSize.append(mEquipment.getSize());
                    eqDesc.append(mEquipment.getDetail());
                    eqAmount.append(String.valueOf(mEquipment.getAmount()));
                    eqPrice.append(numberFormat.format(mEquipment.getPrice()));
                    if (mEquipment.getImage().equals("Default")) {
                        Picasso.get().load(R.drawable.logo_umn).into(eqImg);
                    } else {
                        Picasso.get().load(mEquipment.getImage()).into(eqImg);
                    }
                    if (type == 2) {
                        etAmount.setText(extras.getString("Amount"));
                        etAmount.setEnabled(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etAmount.getText().toString().isEmpty()) {
                        etAmount.setError("Please set the amount to be booked");
                        return;
                    }
                    int bookedAmount = Integer.parseInt(etAmount.getText().toString());
                    if (bookedAmount == 0){
                        etAmount.setError("Please set the amount to be booked");
                        return;
                    }
                    if (bookedAmount > mEquipment.getAmount()) {
                        etAmount.setError("Pass the max value of amount");
                        return;
                    }
                    eqBooked = new EqBooked(mEquipment, bookedAmount);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("ChosenEq", eqBooked);
                    returnIntent.putExtra("ChPos", String.valueOf(extras.getInt("Pos")));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("ChosenEq", mEquipment.getName());
                    returnIntent.putExtra("ChPos", String.valueOf(extras.getInt("Pos")));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
    }
}