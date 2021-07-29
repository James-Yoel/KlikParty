 package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

 public class ExtraNote extends AppCompatActivity {
     EditText etNote;
     Button finish;
     Party party;
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
            SweetAlertDialog sad = new SweetAlertDialog(ExtraNote.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(ExtraNote.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_extra_note);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Notes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Bundle extras = getIntent().getExtras();
            party = (Party) extras.getSerializable("Party");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent prev = new Intent(getApplicationContext(), DateTimeLocation.class);
                    prev.putExtra("Party", party);
                    prev.putExtra("MC", (ArrayList<MC>) extras.getSerializable("MC"));
                    prev.putExtra("Equipment", (ArrayList<EqBooked>) extras.getSerializable("Equipment"));
                    prev.putExtra("prev", extras.getString("prev"));
                    startActivity(prev);
                    finish();
                }
            });
            etNote = findViewById(R.id.etNote);
            finish = findViewById(R.id.noteFinish);
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (party != null) {
                        if (party.getPackageType().equals("Custom Party")) {
                            if (etNote.getText().toString().isEmpty()) {
                                etNote.setError("Please fill in the blank for the custom package party");
                                return;
                            }
                        }
                    }
                    Intent next = new Intent(getApplicationContext(), ReviewOrder.class);
                    next.putExtra("Party", party);
                    next.putExtra("MC", (ArrayList<MC>) extras.getSerializable("MC"));
                    next.putExtra("Equipment", (ArrayList<EqBooked>) extras.getSerializable("Equipment"));
                    next.putExtra("Dtl", (Dtl) extras.getSerializable("Dtl"));
                    next.putExtra("Note", etNote.getText().toString());
                    next.putExtra("prev", extras.getString("prev"));
                    startActivity(next);
                }
            });
        }
    }
}