package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.text.NumberFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HistoryDetail extends AppCompatActivity {
    TextView partyInfo, mcInfo, eqInfo, dtlInfo, noteInfo, orderId, orderMade, bookType, partyPrice, mcPrice, eqPrice, totalPrice;
    LinearLayout cvParty, cvMc, cvEq;
    FirebaseUser user;
    DatabaseReference reference;
    Party party;
    Dtl dtl;
    String note, id;
    Long partyTotal, mcTotal, eqTotal;
    float total;
    NumberFormat numberFormat;
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(HistoryDetail.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(HistoryDetail.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_history_detail);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("History Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            Bundle extras = getIntent().getExtras();
            id = extras.getString("Order");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent prev = new Intent(getApplicationContext(), MainActivity.class);
                    prev.putExtra("prev", "History");
                    startActivity(prev);
                    finish();
                }
            });
            orderId = findViewById(R.id.orderIdInfo);
            orderMade = findViewById(R.id.orderMadeInfo);
            bookType = findViewById(R.id.bookTypeInfo);
            partyInfo = findViewById(R.id.partyInfo);
            mcInfo = findViewById(R.id.mcInfo);
            eqInfo = findViewById(R.id.eqInfo);
            dtlInfo = findViewById(R.id.dtlInfo);
            noteInfo = findViewById(R.id.noteInfo);
            cvParty = findViewById(R.id.cvParty);
            cvMc = findViewById(R.id.cvMC);
            cvEq = findViewById(R.id.cvEq);
            partyPrice = findViewById(R.id.partyHistoryPrice);
            mcPrice = findViewById(R.id.mcHistoryPrice);
            eqPrice = findViewById(R.id.eqHistoryPrice);
            totalPrice = findViewById(R.id.bookTotalPrice);
            user = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Orders").child(user.getUid()).child(id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String type = "Book" + snapshot.child("BookType").getValue(String.class);
                    bookType.setText(type);
                    orderMade.append(snapshot.child("OrderMade").getValue(String.class));
                    orderId.append((String) snapshot.child("ID").getValue(String.class));
                    noteInfo.setText((String) snapshot.child("Note").getValue(String.class));
                    dtl = snapshot.child("Dtl").getValue(Dtl.class);
                    String dInfo = "";
                    assert dtl != null;
                    dInfo += "Date: " + dtl.getDate() + "\n";
                    dInfo += "Start Time: " + dtl.getTime() + "\n";
                    dInfo += "Booked Hour: " + dtl.getHour() + "\n";
                    dInfo += "Location: " + dtl.getLocation();
                    dtlInfo.setText(dInfo);
                    if (snapshot.hasChild("Party")) {
                        party = snapshot.child("Party").getValue(Party.class);
                        String pInfo = "";
                        assert party != null;
                        pInfo += "Party Name: " + party.getName() + "\n";
                        pInfo += "Party Package: " + party.getPackageType() + "\n";
                        pInfo += "Party Scale: " + party.getScale() + "\n";
                        pInfo += "Indoor / Outdoor: " + party.getInOutDoor();
                        partyInfo.setText(pInfo);
                        partyTotal = snapshot.child("PartyPrice").getValue(Long.class);
                        partyPrice.append(numberFormat.format(partyTotal));
                    } else {
                        cvParty.setVisibility(View.GONE);
                        partyTotal = 0L;
                    }
                    if (snapshot.hasChild("MC")) {
                        int i = 1;
                        for (DataSnapshot ds : snapshot.child("MC").getChildren()) {
                            String mInfo = "";
                            if (i != 1) {
                                mInfo += "\n\n";
                            }
                            mInfo += "MC " + i + "\n";
                            mInfo += "MC Name: " + ds.child("Name").getValue(String.class) + "\n";
                            mInfo += "MC Price: " + numberFormat.format(ds.child("Price").getValue()) + "/ hour";
                            i++;
                            mcInfo.append(mInfo);
                        }
                        mcTotal = snapshot.child("MCPrice").getValue(Long.class);
                        mcPrice.append(numberFormat.format(mcTotal));
                    } else {
                        cvMc.setVisibility(View.GONE);
                        mcTotal = 0L;
                    }
                    if (snapshot.hasChild("Equipment")) {
                        int i = 1;
                        for (DataSnapshot ds : snapshot.child("Equipment").getChildren()) {
                            String eInfo = "";
                            if (i != 1) {
                                eInfo += "\n\n";
                            }
                            eInfo += "Equipment " + i + "\n";
                            eInfo += "Equipment Name: " + ds.child("Name").getValue(String.class) + "\n";
                            eInfo += "Equipment Price: " + numberFormat.format(ds.child("Price").getValue()) + "\n";
                            eInfo += "Booked Amount: " + ds.child("Amount").getValue(Long.class);
                            i++;
                            eqInfo.append(eInfo);
                        }
                        eqTotal = snapshot.child("EquipmentPrice").getValue(Long.class);
                        eqPrice.append(numberFormat.format(eqTotal));
                    } else {
                        cvEq.setVisibility(View.GONE);
                        eqTotal = 0L;
                    }
                    Long rTotal = snapshot.child("TotalPrice").getValue(Long.class);
                    if (rTotal!=null){
                        total = rTotal;
                    }
                    else {
                        total = 0f;
                    }
                    totalPrice.append(numberFormat.format(total));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}