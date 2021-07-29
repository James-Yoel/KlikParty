package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.jakewharton.processphoenix.ProcessPhoenix;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookParty extends AppCompatActivity {
    Button finishBook;
    RadioGroup radioGroup;
    RadioButton inOutDoor;
    EditText partyName;
    Spinner partyScale, choosePackage;
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
            SweetAlertDialog sad = new SweetAlertDialog(BookParty.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(BookParty.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_book_party);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Book Party");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
            finishBook = findViewById(R.id.bookFinish);
            radioGroup = findViewById(R.id.radioGroup);
            partyName = findViewById(R.id.partyName);
            partyScale = findViewById(R.id.partyScale);
            choosePackage = findViewById(R.id.choosePackage);
            ArrayAdapter<CharSequence> partyAdapter = ArrayAdapter.createFromResource(this, R.array.partyScale, android.R.layout.simple_spinner_item);
            partyAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            partyScale.setAdapter(partyAdapter);
            ArrayAdapter<CharSequence> packageAdapter = ArrayAdapter.createFromResource(this, R.array.partyPackage, android.R.layout.simple_spinner_item);
            packageAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            choosePackage.setAdapter(packageAdapter);
            choosePackage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        String selected = parent.getItemAtPosition(position).toString();
                        Intent detailPackage = new Intent(getApplicationContext(), DetailPackage.class);
                        detailPackage.putExtra("Package", selected);
                        detailPackage.putExtra("Pos", position);
                        startActivityForResult(detailPackage, 1);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            finishBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String scaleSize = "";
                    String name = partyName.getText().toString();
                    String packageType = choosePackage.getSelectedItem().toString();
                    int selected = radioGroup.getCheckedRadioButtonId();
                    RadioButton inOutDoor = findViewById(selected);
                    String door = inOutDoor.getText().toString();
                    String scale = partyScale.getSelectedItem().toString();
                    Log.i("SCALE", scale);
                    if (name.isEmpty()) {
                        partyName.setError("Party Name is required");
                        return;
                    }
                    if (packageType.equals("Choose Party Package:")) {
                        Toast.makeText(getApplicationContext(), "Please Choose Package First", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (scale.equals("Choose Party Scale:")) {
                        Toast.makeText(getApplicationContext(), "Please Choose Scale First", Toast.LENGTH_LONG).show();
                        return;
                    } else if (scale.equals("Big ($500.00)")) {
                        scaleSize = "Big";
                    } else if (scale.equals("Medium ($300.00)")) {
                        scaleSize = "Medium";
                    } else if (scale.equals("Private ($200.00)")) {
                        scaleSize = "Private";
                    }
                    party = new Party(name, packageType, scaleSize, door);
                    Intent book = new Intent(getApplicationContext(), BookMc.class);
                    book.putExtra("prev", "Party");
                    book.putExtra("Party", party);
                    startActivity(book);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            choosePackage.setSelection(data.getIntExtra("ChosenPackage", 0));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(!partyName.getText().toString().isEmpty()){
            savedInstanceState.putString("Name", partyName.getText().toString());
        }
        else {
            savedInstanceState.putString("Name", null);
        }
        savedInstanceState.putInt("ChoosePackage", choosePackage.getSelectedItemPosition());
        savedInstanceState.putInt("Scale", partyScale.getSelectedItemPosition());
        int selected = radioGroup.getCheckedRadioButtonId();
        savedInstanceState.putInt("InOutDoor",selected);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        partyName.setText(savedInstanceState.getString("Name"));
        choosePackage.setSelection(savedInstanceState.getInt("ChoosePackage"));
        RadioButton door = findViewById(savedInstanceState.getInt("InOutDoor"));
        door.setChecked(true);
    }
}