package com.developeralamin.onlinechating.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.developeralamin.onlinechating.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    EditText login_email, login_password;
    TextView signinBtn, text_signup;
    private ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        signinBtn = findViewById(R.id.signinBtn);
        text_signup = findViewById(R.id.text_signup);

        auth = FirebaseAuth.getInstance();


        text_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLoginBtn();
            }
        });
    }

    private void userLoginBtn() {
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();

        if (email.isEmpty()) {
            login_email.setError("Enter your Email Address");
            login_email.requestFocus();
            return;
        }
        if (!email.matches(emailPattern)) {
            login_email.setError("Please enter Valid Email");
            Toast.makeText(this, "Please enter Valid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            login_password.setError("Enter your Password");
            login_password.requestFocus();
            return;
        }
        if (password.length() > 6) {
            login_password.setError("Please enter Valid Password");
            Toast.makeText(this, "Please enter Valid Password", Toast.LENGTH_SHORT).show();
            return;
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait...........");
            progressDialog.setCancelable(false);
            progressDialog.show();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "error : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}