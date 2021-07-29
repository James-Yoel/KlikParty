package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Register extends AppCompatActivity {
    EditText registerFullname, registerEmail, registerPassword, registerConfPass, registerPhone, registerAddress;
    Button registerBtn, goToLoginBtn;
    FirebaseAuth fAuth;
    DatabaseReference reference;
    Users users;
    CheckConnection checkConnection;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(Register.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(Register.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_register);
            registerFullname = findViewById(R.id.registerFullname);
            registerEmail = findViewById(R.id.registerEmail);
            registerPhone = findViewById(R.id.registerPhone);
            registerAddress = findViewById(R.id.registerAddress);
            registerPassword = findViewById(R.id.registerPassword);
            registerConfPass = findViewById(R.id.confirmationPassword);
            registerBtn = findViewById(R.id.registerBtn);
            goToLoginBtn = findViewById(R.id.goToLoginBtn);
            fAuth = FirebaseAuth.getInstance();

            goToLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            });

            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(Register.this);
                    pd.setMessage("Processing");
                    pd.show();
                    String fullname = registerFullname.getText().toString();
                    Log.i("NAME", fullname);
                    String email = registerEmail.getText().toString();
                    String phone = registerPhone.getText().toString();
                    String address = registerAddress.getText().toString();
                    String password = registerPassword.getText().toString();
                    String confPassword = registerConfPass.getText().toString();
                    if (fullname.isEmpty()) {
                        registerFullname.setError("Username is required");
                        pd.dismiss();
                        return;
                    }
                    if (email.isEmpty()) {
                        registerEmail.setError("Email is required");
                        pd.dismiss();
                        return;
                    }
                    if (phone.isEmpty()) {
                        registerEmail.setError("Phone is required");
                        pd.dismiss();
                        return;
                    }
                    if (address.isEmpty()) {
                        registerEmail.setError("Address is required");
                        pd.dismiss();
                        return;
                    }
                    if (password.isEmpty()) {
                        registerPassword.setError("Password is required");
                        pd.dismiss();
                        return;
                    }
                    if (confPassword.isEmpty()) {
                        registerConfPass.setError("Confirm Password is required");
                        pd.dismiss();
                        return;
                    }
                    if (!password.equals(confPassword)) {
                        registerConfPass.setError("Password did not match");
                        pd.dismiss();
                        return;
                    }
                    fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            assert user != null;
                            String userId = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users");
                            users = new Users(fullname, email, phone, address, "Default", 0);

                            reference.child(userId).setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}