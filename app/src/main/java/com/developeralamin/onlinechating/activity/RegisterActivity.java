package com.developeralamin.onlinechating.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developeralamin.onlinechating.R;
import com.developeralamin.onlinechating.model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView profile_image;
    EditText regName, regEmail, regPassword, regComfirmpassword;
    TextView signupBtn, text_signIn;
    String emailPattern = Patterns.EMAIL_ADDRESS.toString();

    ProgressDialog progressDialog;
    Uri imageUri;
    String imageURI;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profile_image = findViewById(R.id.profile_image);
        regName = findViewById(R.id.regName);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regComfirmpassword = findViewById(R.id.regComfirmpassword);
        signupBtn = findViewById(R.id.signupBtn);
        text_signIn = findViewById(R.id.text_signIn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait......");
        progressDialog.setCancelable(false);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 10);

            }
        });


        text_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignUp();
            }
        });
    }


    private void userSignUp() {
        progressDialog.show();
        String name = regName.getText().toString();
        String email = regEmail.getText().toString();
        String password = regPassword.getText().toString();
        String confirm = regComfirmpassword.getText().toString();
        String status = "Hey There I'm Using this Application";

        if (name.isEmpty()) {
            regName.setError("Enter your Name");
            regName.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (email.isEmpty()) {
            regEmail.setError("Enter your Email");
            regEmail.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (!email.matches(emailPattern)) {
            regEmail.setError("Please Enter Valid Email");
            regEmail.requestFocus();
            progressDialog.dismiss();
            Toast.makeText(this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            regPassword.setError("Enter your Password");
            regPassword.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (confirm.isEmpty()) {
            regComfirmpassword.setError("Enter your Confirm Password");
            regComfirmpassword.requestFocus();
            progressDialog.dismiss();
            return;
        }
        if (!password.equals(confirm)) {
            regComfirmpassword.setError("Password does not match");
            regComfirmpassword.requestFocus();
            progressDialog.dismiss();
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            regPassword.setError("Enter 6 Characher Password");
            regPassword.requestFocus();
            progressDialog.dismiss();
            Toast.makeText(this, "Enter 6 Characher Password", Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressDialog.show();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        DatabaseReference reference = database.getReference().child("FastChat_User").child(auth.getUid());
                        StorageReference storageReference = storage.getReference().child("FastChat_Image").child(auth.getUid());
                        if (imageUri != null) {
                            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageURI = uri.toString();
                                                UserData userData = new UserData(auth.getUid(), name, email, password, imageURI, status);
                                                reference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                                            Toast.makeText(RegisterActivity.this, "Registion Successful", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegisterActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        } else {

                            imageURI = "https://firebasestorage.googleapis.com/v0/b/creativeitapp.appspot.com/o/profile.jpg?alt=media&token=6c8c9bc1-2d65-49fc-a951-afda78751c3d";
                            UserData userData = new UserData(auth.getUid(), name, email, password, imageURI, status);
                            reference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
                imageUri = data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}