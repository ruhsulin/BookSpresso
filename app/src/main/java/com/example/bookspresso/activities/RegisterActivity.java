package com.example.bookspresso.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.bookspresso.R;
import com.example.bookspresso.utils.ToastHelper;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends Activity {
    FirebaseAuth auth;
    public ProgressBar progressBar;
    public EditText etFirstname, etLastname, etEmail, etPassword, etConfirmPassword;
    public Button btnRegister;
    public TextView tvLoginHere;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing variables
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginHere = findViewById(R.id.tvAlreadyHaveAccount);

        // click listeners
        // login
        tvLoginHere.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        // register
        btnRegister.setOnClickListener(view -> registerUser());
    }

    //registering user on Firebase
    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);

        String firstname = etFirstname.getText().toString().trim();
        String lastname = etLastname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInput(firstname, lastname, email, password, confirmPassword)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        ToastHelper.showToast(RegisterActivity.this, "Account Created Successfully!");
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        ToastHelper.showToast(RegisterActivity.this, "Account Creation Failed: " + task.getException().getMessage());
                    }
                });
    }

    // validating inputs
    private boolean validateInput(String firstname, String lastname, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(firstname)) {
            ToastHelper.showToast(this, "Enter first name!");
            return false;
        }
        if (TextUtils.isEmpty(lastname)) {
            ToastHelper.showToast(this, "Enter last name!");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            ToastHelper.showToast(this, "Enter email!");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ToastHelper.showToast(this, "Enter a valid email address!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ToastHelper.showToast(this, "Enter password!");
            return false;
        }
        if (password.length() < 6) {
            ToastHelper.showToast(this, "Password must be at least 6 characters!");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            ToastHelper.showToast(this, "Passwords do not match!");
            return false;
        }
        return true;
    }

    // lifecycle onDestroy()
    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth = null;  // prevent memory leak
    }
}
