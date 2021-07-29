package umn.ac.id.uas_mobileandroid_2021_a3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfile extends AppCompatActivity {
    Button commitChangeBtn, cancelBtn;
    EditText editUsername, editEmail, editPhone, editAddress;
    CircularImageView editImg;
    Users user, newUser;
    String generatedFilePath;
    String username, email, phone, address;
    ProgressDialog pd;
    boolean change = false;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private Uri uri;
    private static final int REQUEST_IMG = 1;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, newReference;
    CheckConnection checkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection = new CheckConnection();
        if (!checkConnection.checkNetwork(getApplicationContext())) {
            SweetAlertDialog sad = new SweetAlertDialog(EditProfile.this, SweetAlertDialog.ERROR_TYPE);
            sad.setTitleText("Oops...");
            sad.setContentText("Something went wrong! Please check your connection.");
            sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ProcessPhoenix.triggerRebirth(EditProfile.this);
                }
            });
            sad.setCancelable(false);
            sad.show();
        } else {
            setContentView(R.layout.activity_edit_profile);
            Toolbar toolbar = findViewById(R.id.appToolbar);
            setSupportActionBar(toolbar);
            TextView title = findViewById(R.id.toolbarTitle);
            title.setText("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            editUsername = findViewById(R.id.editUsername);
            editEmail = findViewById(R.id.editEmail);
            editPhone = findViewById(R.id.editPhone);
            editAddress = findViewById(R.id.editAddress);
            commitChangeBtn = findViewById(R.id.commitChangeBtn);
            cancelBtn = findViewById(R.id.cancelBtn);
            editImg = findViewById(R.id.editImg);
            user = new Users();
            firebaseStorage = FirebaseStorage.getInstance();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            Log.i("USR", firebaseUser.getUid());
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                }
            });
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(Users.class);
                    assert user != null;
                    editUsername.setText(user.getUsername());
                    editEmail.setText(user.getEmail());
                    editPhone.setText(user.getPhone());
                    editAddress.setText(user.getAddress());
                    if (user.getImage().equals("Default")) {
                        Picasso.get().load(R.drawable.default_profile).into(editImg);
                    } else {
                        Picasso.get().load(user.getImage()).into(editImg);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            editImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_IMG);
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                }
            });
            commitChangeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd = new ProgressDialog(EditProfile.this);
                    pd.setMessage("Uploading");
                    pd.show();
                    username = editUsername.getText().toString();
                    email = editEmail.getText().toString();
                    phone = editPhone.getText().toString();
                    address = editAddress.getText().toString();
                    if (username.isEmpty()) {
                        editUsername.setError("Fullname is required");
                        pd.dismiss();
                        return;
                    }
                    if (email.isEmpty()) {
                        editEmail.setError("Email is required");
                        pd.dismiss();
                        return;
                    }
                    if (phone.isEmpty()) {
                        editPhone.setError("Phone is required");
                        pd.dismiss();
                        return;
                    }
                    if (address.isEmpty()) {
                        editEmail.setError("Address is required");
                        pd.dismiss();
                        return;
                    }
                    if (change) {
                        uploadFile();
                    } else {
                        generatedFilePath = user.getImage();
                        assert firebaseUser != null;
                        String userId = firebaseUser.getUid();
                        newReference = FirebaseDatabase.getInstance().getReference("Users");
                        newUser = new Users(username, email, phone, address, generatedFilePath, user.getBalance());
                        newReference.child(userId).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                startActivity(new Intent(getApplicationContext(), MyProfile.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    public void uploadFile(){
        String img = System.currentTimeMillis()+"."+getExtension(uri);
        storageReference = firebaseStorage.getReference().child(img);
        storageTask = storageReference.putFile(uri);
        storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Log.i("CHC", "SUCCESS2");
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    generatedFilePath = downloadUri.toString();
                    Log.i("IMG", generatedFilePath);
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();
                    newReference = FirebaseDatabase.getInstance().getReference("Users");
                    newUser = new Users(username, email, phone, address, generatedFilePath, user.getBalance());
                    Log.i("CHC", "SUCEESS3");
                    pd.dismiss();
                    newReference.child(userId).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            startActivity(new Intent(getApplicationContext(), MyProfile.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    pd.dismiss();
                    startActivity(new Intent(getApplicationContext(), MyProfile.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        });
    }

    private String getExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMG && resultCode == RESULT_OK && data != null && data.getData() != null){
             uri = data.getData();
             Picasso.get().load(uri).into(editImg);
             //editImg.setImageURI(uri);
             change = true;
             Log.i("URI", uri.toString());
        }
    }
}