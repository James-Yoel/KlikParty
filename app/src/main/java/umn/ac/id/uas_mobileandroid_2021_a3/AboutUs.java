package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.processphoenix.ProcessPhoenix;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AboutUs extends AppCompatActivity {
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(AboutUs.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(AboutUs.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_about_us);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("About Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentMap, new MapsActivity()).commit();
        }
    }
}