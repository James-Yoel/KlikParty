package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DateTimeLocation extends AppCompatActivity {
    EditText bookDate, bookTime, bookLocation;
    Spinner bookHour;
    Button finish;
    TextView myAddress;
    Dtl dtl;
    Party party;
    ArrayList<MC> mc;
    ArrayList<EqBooked> equipment;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
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
            SweetAlertDialog sad = new SweetAlertDialog(DateTimeLocation.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(DateTimeLocation.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_date_time_location);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Date Time Location");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Bundle extras = getIntent().getExtras();
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent prev;
                    switch (extras.getString("prev")) {
                        case "Party":
                            prev = new Intent(getApplicationContext(), BookEquipment.class);
                            prev.putExtra("prev", extras.getString("prev"));
                            prev.putExtra("Party", (Party) extras.getSerializable("Party"));
                            prev.putExtra("MC", (ArrayList<MC>) extras.getSerializable("MC"));
                            startActivity(prev);
                            break;
                        case "MC":
                            prev = new Intent(getApplicationContext(), BookMc.class);
                            prev.putExtra("prev", extras.getString("prev"));
                            startActivity(prev);
                            break;
                        case "Equipment":
                            prev = new Intent(getApplicationContext(), BookEquipment.class);
                            prev.putExtra("prev", extras.getString("prev"));
                            startActivity(prev);
                            break;
                    }
                    finish();
                }
            });
            bookDate = findViewById(R.id.dateBook);
            bookTime = findViewById(R.id.timeBook);
            bookLocation = findViewById(R.id.locationBook);
            bookHour = findViewById(R.id.hourBook);
            finish = findViewById(R.id.dtlFinish);
            myAddress = findViewById(R.id.fromProfAddress);
            switch (extras.getString("prev")) {
                case "Party":
                    party = (Party) extras.getSerializable("Party");
                    mc = (ArrayList<MC>) extras.getSerializable("MC");
                    equipment = (ArrayList<EqBooked>) extras.getSerializable("Equipment");
                    break;
                case "MC":
                    party = null;
                    mc = (ArrayList<MC>) extras.getSerializable("MC");
                    equipment = null;
                    break;
                case "Equipment":
                    party = null;
                    mc = null;
                    equipment = (ArrayList<EqBooked>) extras.getSerializable("Equipment");
                    break;
            }
            ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(this, R.array.bookingHour, android.R.layout.simple_spinner_item);
            hourAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            bookHour.setAdapter(hourAdapter);
            bookDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDateDialog(bookDate);
                }
            });
            bookTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDialog(bookTime);
                }
            });
            myAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String location = snapshot.child("address").getValue().toString();
                            bookLocation.setText(location);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String date = bookDate.getText().toString();
                    String time = bookTime.getText().toString();
                    String location = bookLocation.getText().toString();
                    String hour = bookHour.getSelectedItem().toString();
                    if (date.isEmpty()) {
                        bookDate.setError("Date is required");
                        return;
                    }
                    if (time.isEmpty()) {
                        bookTime.setError("Start Time is required");
                        return;
                    }
                    if (location.isEmpty()) {
                        bookLocation.setError("Location is required");
                        return;
                    }
                    dtl = new Dtl(date, time, hour, location);
                    Intent next = new Intent(getApplicationContext(), ExtraNote.class);
                    next.putExtra("Party", party);
                    next.putExtra("MC", mc);
                    next.putExtra("Equipment", equipment);
                    next.putExtra("Dtl", dtl);
                    next.putExtra("prev", extras.getString("prev"));
                    startActivity(next);
                }
            });
        }
    }

    private void showTimeDialog(EditText bookTime){
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
                bookTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new TimePickerDialog(DateTimeLocation.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void showDateDialog(EditText bookDate){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                bookDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(DateTimeLocation.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.YEAR, 1);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }
}