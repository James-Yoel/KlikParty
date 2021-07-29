package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.processphoenix.ProcessPhoenix;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {
    Button createAccoutBtn, loginBtn;
    TextView forgotPassword;
    EditText username, password;
    FirebaseAuth firebaseAuth;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    CheckConnection checkConnection;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if(!checkConnection.checkNetwork(Login.this)){
            SweetAlertDialog sad = new SweetAlertDialog(Login.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(Login.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        }
        else{
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            setContentView(R.layout.activity_login);
            createAccoutBtn = findViewById(R.id.createAccountBtn);
            username = findViewById(R.id.loginEmail);
            password = findViewById(R.id.loginPassword);
            loginBtn = findViewById(R.id.loginBtn);
            forgotPassword = findViewById(R.id.forgotPassword);
            firebaseAuth = FirebaseAuth.getInstance();
            reset_alert = new AlertDialog.Builder(Login.this);
            inflater = this.getLayoutInflater();
            createAccoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    finish();
                }
            });
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(Login.this);
                    pd.setMessage("Processing");
                    pd.show();
                    if(username.getText().toString().isEmpty()){
                        username.setError("Email is missing");
                        pd.dismiss();
                        return;
                    }
                    if(password.getText().toString().isEmpty()){
                        password.setError("Password is missing");
                        pd.dismiss();
                        return;
                    }
                    firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            pd.dismiss();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = inflater.inflate(R.layout.forgot_password_popup, null);
                    reset_alert.setTitle("Reset Forgot Password ?").setMessage("Enter your email to get Password Link")
                            .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText email = view.findViewById(R.id.resetEmail);
                                    if(email.getText().toString().isEmpty()){
                                        email.setError("Required Field");
                                        return;
                                    }
                                    firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Login.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", null).setView(view).create().show();
                }
            });
        }
    }
}