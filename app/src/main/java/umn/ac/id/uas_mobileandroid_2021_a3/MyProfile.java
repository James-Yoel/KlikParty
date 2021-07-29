package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyProfile extends AppCompatActivity {
    Button chargeBtn, editProfileBtn, changePasswordBtn;
    TextView profilUsername, profilEmail, profilPhone, profilAddress, profilBalance;
    Users user;
    CircularImageView userImg;
    LayoutInflater inflater;
    FirebaseAuth firebaseAuth;
    AlertDialog.Builder reset_alert;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private NumberFormat numberFormat;
    ProgressDialog pd;
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(MyProfile.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(MyProfile.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_my_profile);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            profilUsername = findViewById(R.id.profilUsername);
            profilEmail = findViewById(R.id.profilEmail);
            profilPhone = findViewById(R.id.profilPhone);
            profilAddress = findViewById(R.id.profilAddress);
            profilBalance = findViewById(R.id.balanceProfile);
            chargeBtn = findViewById(R.id.chargeBalance);
            editProfileBtn = findViewById(R.id.editProfile);
            changePasswordBtn = findViewById(R.id.changePass);
            userImg = findViewById(R.id.userImg);
            user = new Users();
            firebaseAuth = FirebaseAuth.getInstance();
            reset_alert = new AlertDialog.Builder(MyProfile.this);
            inflater = this.getLayoutInflater();
            firebaseUser = firebaseAuth.getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            Log.i("USR", firebaseUser.getUid());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
            TextView logout = findViewById(R.id.toolbarText);
            logout.setVisibility(View.VISIBLE);
            logout.setText("Logout");
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(MyProfile.this)
                            .setTitleText("Log Out")
                            .setContentText("Are You Sure?")
                            .setConfirmText("Yes")
                            .showCancelButton(true)
                            .setCancelText("Cancel")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                    finish();
                                }
                            })
                            .show();
                }
            });
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(Users.class);
                    assert user != null;
                    profilUsername.setText(user.getUsername());
                    profilEmail.setText(user.getEmail());
                    profilPhone.setText(user.getPhone());
                    profilAddress.setText(user.getAddress());
                    profilBalance.setText(numberFormat.format(user.getBalance()));
                    if (user.getImage().equals("Default")) {
                        Picasso.get().load(R.drawable.default_profile).into(userImg);
                    } else {
                        Picasso.get().load(user.getImage()).into(userImg);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            editProfileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));
                }
            });
            changePasswordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = inflater.inflate(R.layout.forgot_password_popup, null);
                    reset_alert.setTitle("Reset Change Password ?").setMessage("Enter your email to get Password Link")
                            .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText email = view.findViewById(R.id.resetEmail);
                                    if (email.getText().toString().isEmpty()) {
                                        email.setError("Required Field");
                                        return;
                                    }
                                    firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MyProfile.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MyProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", null).setView(view).create().show();
                }
            });
            chargeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = inflater.inflate(R.layout.topup_balance_popup, null);
                    reset_alert.setTitle("Top-Up Balance").setMessage("Current Balance: " + numberFormat.format(user.getBalance()))
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pd = new ProgressDialog(MyProfile.this);
                                    pd.setMessage("Processing");
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
                                    float total = topup + user.getBalance();
                                    reference.child("balance").setValue(total).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(MyProfile.this, "Top-Up Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(MyProfile.this, "Top-Up Failed. Please try again later", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", null).setView(view).create().show();
                }
            });
        }
    }
}