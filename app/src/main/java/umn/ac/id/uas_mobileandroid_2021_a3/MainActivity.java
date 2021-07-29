 package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    TextView username, balance;
    Users user;
    NumberFormat numberFormat;
    CircularImageView userImg;
    BottomNavigationView bottomNavigationView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    ProgressDialog pd;
    CheckConnection checkConnection;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(MainActivity.this)) {
            SweetAlertDialog sad = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(MainActivity.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.appToolbar2);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            userImg = findViewById(R.id.userImg);
            username = findViewById(R.id.username);
            balance = findViewById(R.id.balance);
            bottomNavigationView = findViewById(R.id.mainBottom);
            user = new Users();
            numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            Log.i("USR", firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    pd = new ProgressDialog(MainActivity.this);
                    pd.setMessage("Loading");
                    pd.show();
                    user = snapshot.getValue(Users.class);
                    assert user != null;
                    username.setText(user.getUsername());
                    String deck = "Balance: " + numberFormat.format(user.getBalance());
                    balance.setText(deck);
                    if (user.getImage().equals("Default")) {
                        Picasso.get().load(R.drawable.default_profile).into(userImg);
                    } else {
                        Picasso.get().load(user.getImage()).into(userImg);
                    }
                    pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
            Intent intent = getIntent();
            if (intent.hasExtra("prev")) {
                if (intent.getStringExtra("prev").equals("History")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HistoryFragment()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
                }
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
            }
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            switch (item.getItemId()){
                case R.id.navHome:
                    selected = new HomeFragment();
                    break;
                case R.id.navHistory:
                    selected = new HistoryFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selected).commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.myProfile:
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
                return true;
            case R.id.aboutUs:
                startActivity(new Intent(getApplicationContext(), AboutUs.class));
                return true;
            case R.id.menuLogout:
                new SweetAlertDialog(MainActivity.this)
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}