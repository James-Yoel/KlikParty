package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;

public class ReviewOrder extends AppCompatActivity {
    TextView partyInfo, mcInfo, eqInfo, dtlInfo, noteInfo, partyPrice, mcPrice, eqPrice, totalPrice;
    LinearLayout cvParty, cvMc, cvEq;
    Users users;
    Button finish;
    Party party;
    ArrayList<MC> mc;
    ArrayList<EqBooked> eqBooked;
    Dtl dtl;
    String note;
    FirebaseUser user;
    DatabaseReference reference, userReference;
    String id;
    int hour = 0;
    float partyTotal = 0, mcTotal = 0, eqTotal = 0, total= 0, balance = 0;
    NumberFormat numberFormat;
    LayoutInflater inflater;
    AlertDialog.Builder reset_alert;
    ProgressDialog pd;
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(ReviewOrder.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(ReviewOrder.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_review_order);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Review Order");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            user = FirebaseAuth.getInstance().getCurrentUser();
            numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            Bundle extras = getIntent().getExtras();
            party = (Party) extras.getSerializable("Party");
            mc = (ArrayList<MC>) extras.getSerializable("MC");
            eqBooked = (ArrayList<EqBooked>) extras.getSerializable("Equipment");
            dtl = (Dtl) extras.getSerializable("Dtl");
            note = extras.getString("Note");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent prev = new Intent(getApplicationContext(), ExtraNote.class);
                    prev.putExtra("Party", party);
                    prev.putExtra("MC", mc);
                    prev.putExtra("Equipment", eqBooked);
                    prev.putExtra("Dtl", dtl);
                    prev.putExtra("prev", extras.getString("prev"));
                    startActivity(prev);
                    finish();
                }
            });
            partyInfo = findViewById(R.id.partyInfo);
            mcInfo = findViewById(R.id.mcInfo);
            eqInfo = findViewById(R.id.eqInfo);
            dtlInfo = findViewById(R.id.dtlInfo);
            noteInfo = findViewById(R.id.noteInfo);
            cvParty = findViewById(R.id.cvParty);
            cvMc = findViewById(R.id.cvMC);
            cvEq = findViewById(R.id.cvEq);
            partyPrice = findViewById(R.id.partyReviewPrice);
            mcPrice = findViewById(R.id.mcReviewPrice);
            eqPrice = findViewById(R.id.eqReviewPrice);
            totalPrice = findViewById(R.id.reviewTotalPrice);
            finish = findViewById(R.id.reviewFinish);
            hour = Integer.parseInt(dtl.getHour());
            reset_alert = new AlertDialog.Builder(ReviewOrder.this);
            inflater = this.getLayoutInflater();
            userReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users = snapshot.getValue(Users.class);
                    assert users != null;
                    balance = users.getBalance();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (party == null) {
                cvParty.setVisibility(View.GONE);
            } else {
                String pInfo = "";
                pInfo += "Party Name: " + party.getName() + "\n";
                pInfo += "Party Package: " + party.getPackageType() + "\n";
                pInfo += "Party Scale: " + party.getScale() + "\n";
                pInfo += "Indoor / Outdoor: " + party.getInOutDoor();
                partyInfo.setText(pInfo);
                Log.i("SCALE", party.getScale());
                if (party.getScale().equals("Big")) {
                    partyTotal = 500;
                    partyPrice.append(numberFormat.format(500));
                } else if (party.getScale().equals("Medium")) {
                    partyTotal = 300;
                    partyPrice.append(numberFormat.format(300));
                } else {
                    partyTotal = 200;
                    partyPrice.append(numberFormat.format(200));
                }
            }
            if (mc == null) {
                cvMc.setVisibility(View.GONE);
            } else {
                int i = 1;
                float price;
                for (MC mMc : mc) {
                    String mInfo = "";
                    if (i != 1) {
                        mInfo += "\n\n";
                    }
                    mInfo += "MC " + i + "\n";
                    mInfo += "MC Name: " + mMc.getName() + "\n";
                    mInfo += "MC Price: " + numberFormat.format(mMc.getPrice()) + "/ hour";
                    i++;
                    mcInfo.append(mInfo);
                    price = mMc.getPrice() * hour;
                    mcTotal += price;
                }
                mcPrice.append(numberFormat.format(mcTotal));
            }
            if (eqBooked == null) {
                cvEq.setVisibility(View.GONE);
            } else {
                int i = 1;
                float price;
                for (EqBooked eqBooked1 : eqBooked) {
                    String eInfo = "";
                    if (i != 1) {
                        eInfo += "\n\n";
                    }
                    eInfo += "Equipment " + i + "\n";
                    eInfo += "Equipment Name: " + eqBooked1.getEquipment().getName() + "\n";
                    eInfo += "Equipment Price: " + numberFormat.format(eqBooked1.getEquipment().getPrice()) + "\n";
                    eInfo += "Booked Amount: " + eqBooked1.getAmount();
                    i++;
                    eqInfo.append(eInfo);
                    price = eqBooked1.getEquipment().getPrice() * eqBooked1.getAmount();
                    eqTotal += price;
                }
                eqPrice.append(numberFormat.format(eqTotal));
            }
            String dInfo = "";
            dInfo += "Date: " + dtl.getDate() + "\n";
            dInfo += "Start Time: " + dtl.getTime() + "\n";
            dInfo += "Booked Hour: " + dtl.getHour() + "\n";
            dInfo += "Location: " + dtl.getLocation();
            dtlInfo.setText(dInfo);
            if (note.trim().equals("")) {
                noteInfo.setText("No Extra Note.");
            } else {
                noteInfo.setText(note);
            }
            total = partyTotal + mcTotal + eqTotal;
            totalPrice.append(numberFormat.format(total));
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(ReviewOrder.this);
                    pd.setMessage("Processing");
                    pd.show();
                    if (balance < total) {
                        pd.dismiss();
                        View view = inflater.inflate(R.layout.topup_balance_popup, null);
                        reset_alert.setTitle("Top-Up Balance").setMessage("Current Balance: " + numberFormat.format(users.getBalance()))
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pd.show();
                                        EditText amount = view.findViewById(R.id.topupBalance);
                                        if (amount.getText().toString().isEmpty()) {
                                            amount.setError("Required Field");
                                            pd.dismiss();
                                            return;
                                        }
                                        if (Float.parseFloat(amount.getText().toString()) == 0) {
                                            amount.setError("Can't be 0");
                                            pd.dismiss();
                                            return;
                                        }
                                        float topup = Float.parseFloat(amount.getText().toString());
                                        float total = topup + balance;
                                        Log.i("REF", userReference.toString());
                                        userReference.child("balance").setValue(total).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(ReviewOrder.this, "Top-Up Successful", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(ReviewOrder.this, "Top-Up Failed. Please try again later", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).setNegativeButton("Cancel", null).setView(view).create().show();
                    } else {
                        reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(user.getUid());
                        id = "OR" + System.currentTimeMillis();
                        Calendar calendar = Calendar.getInstance();
                        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);
                        String orderMade = dateFormat.format(calendar.getTime());
                        if (party != null) {
                            reference.child(id).child("Party").setValue(party);
                            reference.child(id).child("PartyPrice").setValue(partyTotal);
                        }
                        if (mc != null) {
                            for (int i = 0; i < mc.size(); i++) {
                                MC mMc = mc.get(i);
                                reference.child(id).child("MC").child(String.valueOf(i)).child("Name").setValue(mMc.getName());
                                reference.child(id).child("MC").child(String.valueOf(i)).child("Price").setValue(mMc.getPrice());
                            }
                            reference.child(id).child("MCPrice").setValue(mcTotal);
                        }
                        if (eqBooked != null) {
                            for (int i = 0; i < eqBooked.size(); i++) {
                                EqBooked eqBooked1 = eqBooked.get(i);
                                reference.child(id).child("Equipment").child(String.valueOf(i)).child("Name").setValue(eqBooked1.getEquipment().getName());
                                reference.child(id).child("Equipment").child(String.valueOf(i)).child("Price").setValue(eqBooked1.getEquipment().getPrice());
                                reference.child(id).child("Equipment").child(String.valueOf(i)).child("Amount").setValue(eqBooked1.getAmount());
                            }
                            reference.child(id).child("EquipmentPrice").setValue(eqTotal);
                        }
                        reference.child(id).child("Dtl").setValue(dtl);
                        reference.child(id).child("ID").setValue(id);
                        reference.child(id).child("BookType").setValue(extras.getString("prev"));
                        reference.child(id).child("OrderMade").setValue(orderMade);
                        reference.child(id).child("TotalPrice").setValue(total);
                        reference.child(id).child("Note").setValue(noteInfo.getText()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                userReference.child("balance").setValue(balance - total);
                                final SweetAlertDialog sd = new SweetAlertDialog(ReviewOrder.this, SweetAlertDialog.SUCCESS_TYPE);
                                        sd.setTitleText("Order made successfully!");
                                        sd.setConfirmText("Return to home");
                                        sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismissWithAnimation();
                                                Intent intent = new Intent("finish_activity");
                                                sendBroadcast(intent);
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }
                                        });
                                        sd.setCancelable(false);
                                        sd.show();
                                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(ReviewOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}