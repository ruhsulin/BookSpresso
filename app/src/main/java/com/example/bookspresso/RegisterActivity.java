package com.example.bookspresso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends Activity {
    FirebaseAuth auth;
    public ProgressBar progressBar;
    public EditText etFirstname, etLastname, etEmail, etPassword;
    public Button btnRegister;
    public TextView tvLoginHere;

    // Initialization
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginHere = findViewById(R.id.tvAlreadyHaveAccount);

        tvLoginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Register user to Firebase
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                String firstname, lastname, email, password;
                firstname = etFirstname.getText().toString();
                lastname = etLastname.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (TextUtils.isEmpty(firstname)) {
                    showToast("Enter first name!");
                    return;
                }

                if (TextUtils.isEmpty(lastname)) {
                    showToast("Enter last name!");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    showToast("Enter email!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    showToast("Enter password!");
                    return;
                }

                // Register user in Firebase
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    showToast("Account Created Successfully!");
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    showToast("Account Creation Failed!");
                                }
                            }
                        });
            }
        });
        }

        private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

