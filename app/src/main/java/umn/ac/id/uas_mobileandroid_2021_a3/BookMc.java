package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookMc extends AppCompatActivity {
    McAdapter mcAdapter;
    RecyclerView recyclerView;
    Party party;
    private DatabaseReference reference;
    List<MC> mMc;
    List<String> chooseMc, chosen;
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
            SweetAlertDialog sad = new SweetAlertDialog(BookMc.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(BookMc.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_book_mc);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Book MC");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Bundle extras = getIntent().getExtras();
            if (extras.getString("prev").equals("Party")) {
                party = (Party) extras.getSerializable("Party");
                TextView skip = findViewById(R.id.toolbarText);
                skip.setVisibility(View.VISIBLE);
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent skip = new Intent(getApplicationContext(), BookEquipment.class);
                        skip.putExtra("prev", "Party");
                        skip.putExtra("Party", party);
                        skip.putExtra("MC", (java.io.Serializable) null);
                        startActivity(skip);
                        finish();
                    }
                });
            } else {
                party = null;
            }
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (extras.getString("prev").equals("Party")) {
                        startActivity(new Intent(getApplicationContext(), BookParty.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            });
            recyclerView = findViewById(R.id.recyclerMc);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 120));
            finish = findViewById(R.id.bookMcFinish);
            chooseMc = new ArrayList<>();
            chosen = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference("MC");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mMc = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        MC mc = ds.getValue(MC.class);
                        mMc.add(mc);
                    }
                    mcAdapter = new McAdapter(getApplicationContext(), BookMc.this, mMc, chosen);
                    recyclerView.setAdapter(mcAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chooseMc.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Please choose MC first.", Toast.LENGTH_SHORT).show();
                    } else {
                        ArrayList<MC> chMc = new ArrayList<>();
                        for (int i = 0; i < mMc.size(); i++) {
                            for (int j = 0; j < chooseMc.size(); j++) {
                                if (mMc.get(i).getName().equals(chooseMc.get(j))) {
                                    chMc.add(mMc.get(i));
                                    Log.i("MC", mMc.get(i).getName());
                                }
                            }
                        }
                        Intent next;
                        if (extras.getString("prev").equals("Party")) {
                            next = new Intent(getApplicationContext(), BookEquipment.class);
                            next.putExtra("prev", "Party");
                        } else {
                            next = new Intent(getApplicationContext(), DateTimeLocation.class);
                            next.putExtra("prev", "MC");
                        }
                        next.putExtra("Party", party);
                        next.putExtra("MC", chMc);
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
            chooseMc.add(extras.getString("ChosenMC"));
            chosen.add(extras.getString("ChPos"));
            mcAdapter.notifyDataSetChanged();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            Bundle extras = data.getExtras();
            chooseMc.remove(extras.getString("ChosenMC"));
            chosen.remove(extras.getString("ChPos"));
            mcAdapter.notifyDataSetChanged();
        }
    }
}