package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookEquipment extends AppCompatActivity {

    EquipmentAdapter eqAdapter;
    RecyclerView recyclerView;
    Party party;
    ArrayList<MC> mc;
    ArrayList<EqBooked> eqBooked;
    private DatabaseReference reference;
    List<Equipment> mEquipment;
    List<String> chosen;
    Button finish;
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(BookEquipment.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(BookEquipment.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_book_equipment);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Book Equipment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Bundle extras = getIntent().getExtras();
            if (extras.getString("prev").equals("Party")) {
                party = (Party) extras.getSerializable("Party");
                mc = (ArrayList<MC>) extras.getSerializable("MC");
                TextView skip = findViewById(R.id.toolbarText);
                skip.setVisibility(View.VISIBLE);
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent skip = new Intent(getApplicationContext(), DateTimeLocation.class);
                        skip.putExtra("prev", "Party");
                        skip.putExtra("Party", party);
                        skip.putExtra("MC", mc);
                        skip.putExtra("Equipment", (java.io.Serializable) null);
                        startActivity(skip);
                        finish();
                    }
                });
            } else {
                party = null;
                mc = null;
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (extras.getString("prev").equals("Party")) {
                        Intent prev = new Intent(getApplicationContext(), BookMc.class);
                        prev.putExtra("prev", extras.getString("prev"));
                        prev.putExtra("Party", (Party) extras.getSerializable("Party"));
                        startActivity(prev);
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            });
            eqBooked = new ArrayList<>();
            recyclerView = findViewById(R.id.recyclerEq);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 120));
            finish = findViewById(R.id.bookEqFinish);
            chosen = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference("Equipment");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mEquipment = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Equipment equipment = ds.getValue(Equipment.class);
                        mEquipment.add(equipment);
                    }
                    eqAdapter = new EquipmentAdapter(getApplicationContext(), BookEquipment.this, mEquipment, chosen, eqBooked);
                    recyclerView.setAdapter(eqAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eqBooked.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Please choose Equipment first.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent next = new Intent(getApplicationContext(), DateTimeLocation.class);
                        if (extras.getString("prev").equals("Party")) {
                            next.putExtra("prev", "Party");
                        } else {
                            next.putExtra("prev", "Equipment");
                        }
                        next.putExtra("Party", party);
                        next.putExtra("MC", mc);
                        next.putExtra("Equipment", eqBooked);
                        startActivity(next);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            Bundle extras = data.getExtras();
            eqBooked.add((EqBooked) extras.getSerializable("ChosenEq"));
            chosen.add(extras.getString("ChPos"));
            eqAdapter.notifyDataSetChanged();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            Bundle extras = data.getExtras();
            String remove = extras.getString("ChosenEq");
            for (int i = 0; i < eqBooked.size(); i++){
                if(eqBooked.get(i).getEquipment().getName().equals(remove)){
                    eqBooked.remove(i);
                    chosen.remove(extras.getString("ChPos"));
                    break;
                }
            }
            eqAdapter.notifyDataSetChanged();
        }
    }
}