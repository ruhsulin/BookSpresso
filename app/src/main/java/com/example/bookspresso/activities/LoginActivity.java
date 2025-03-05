package com.example.bookspresso.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bookspresso.R;
import com.example.bookspresso.utils.ToastHelper;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initializing Firebase
        auth = FirebaseAuth.getInstance();

        //mapping UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterHere = findViewById(R.id.tvAlreadyHaveAccount);
        progressBar = findViewById(R.id.progressBar);

        // Check if user is Logged in
        checkUserSession();

        // Click Listeners
        //login
        btnLogin.setOnClickListener(view -> loginUser());

        // register
        tvRegisterHere.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    // loginUser method
    public void loginUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //Validating email and password
        if (!validateInput(email, password)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        //sign in with Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        ToastHelper.showToast(LoginActivity.this, "Login Successful!");

                        // Save User Session
                        String userId = auth.getCurrentUser().getUid();
                        saveUserSession(userId);
                        saveUserEmail(email);

                        // Navigate to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastHelper.showToast(LoginActivity.this, "Login Failed: " + task.getException().getMessage());
                    }
                });
    }

    // checkUserSession
    private void checkUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("BookSpresso", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("Email", "");

        if (!TextUtils.isEmpty(savedEmail)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", savedEmail);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    // saveUserSession
    private void saveUserSession(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    // saveUserEmail
    private void saveUserEmail(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("BookSpresso", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.apply();
    }

    // Validating inputs
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            ToastHelper.showToast(this, "Please enter your email");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ToastHelper.showToast(this, "Enter a valid email address!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ToastHelper.showToast(this, "Please enter your password");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        auth = null; // Prevent memory leaks
    }
}