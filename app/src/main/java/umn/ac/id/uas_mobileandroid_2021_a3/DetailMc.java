package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class DetailMc extends AppCompatActivity {

    TextView mcDetailName, mcDesc, mcAge, mcGender, mcPersonality, mcPrice;
    ImageView mcImage;
    MC mMc;
    Button finish, remove;
    NumberFormat numberFormat;
    private DatabaseReference reference;
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(DetailMc.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(DetailMc.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_detail_mc);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("MC Details");
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
            String mcPick = extras.getString("MC");
            int type = extras.getInt("Type");
            mMc = new MC();
            mcDetailName = findViewById(R.id.mcDetailName);
            mcImage = findViewById(R.id.mcDetailImg);
            mcAge = findViewById(R.id.mcAge);
            mcGender = findViewById(R.id.mcGender);
            mcPersonality = findViewById(R.id.mcPersonality);
            mcPrice = findViewById(R.id.mcDetailPrice);
            mcDesc = findViewById(R.id.mcDetail);
            finish = findViewById(R.id.mcFinish);
            remove = findViewById(R.id.mcRemove);
            if (type == 2) {
                remove.setVisibility(View.VISIBLE);
                finish.setVisibility(View.GONE);
            } else {
                finish.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
            reference = FirebaseDatabase.getInstance().getReference("MC").child(mcPick);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mMc = snapshot.getValue(MC.class);
                    mcDetailName.setText(mMc.getName());
                    mcAge.append(mMc.getAge());
                    mcGender.append(mMc.getGender());
                    mcPersonality.append(mMc.getPersonality());
                    mcDesc.append(mMc.getDetail());
                    mcPrice.append(numberFormat.format(mMc.getPrice()) + " / hour");
                    if (mMc.getImage().equals("Default")) {
                        Picasso.get().load(R.drawable.logo_umn).into(mcImage);
                    } else {
                        Picasso.get().load(mMc.getImage()).into(mcImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("ChosenMC", mMc.getName());
                    returnIntent.putExtra("ChPos", String.valueOf(extras.getInt("Pos")));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("ChosenMC", mMc.getName());
                    returnIntent.putExtra("ChPos", String.valueOf(extras.getInt("Pos")));
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
    }
}