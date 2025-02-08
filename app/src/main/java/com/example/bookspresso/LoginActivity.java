package com.example.bookspresso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterHere;
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
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterHere = findViewById(R.id.tvAlreadyHaveAccount);
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences preferences = getSharedPreferences("BookSpresso", Context.MODE_PRIVATE);
        String savedEmail = preferences.getString("Email", "");

        if (!TextUtils.isEmpty(savedEmail)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        savedEmail = ReadSharedPreferences();
        if (!TextUtils.isEmpty(savedEmail)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", savedEmail);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        tvRegisterHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
    // loginUser method
    public void loginUser(){
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //Validating email and password
        if (TextUtils.isEmpty(email)) {
            showToast("Please enter your email");
            return;
        }
        if(TextUtils.isEmpty(password)){
            showToast("Please enter your password");
            return;
        }

        progressBar.setVisibility((View.VISIBLE));

        //sign user with Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if(task.isSuccessful()){
                            showToast("Login Successful!");

                            //Saving userId in SharedPReferences
                            String userId = auth.getCurrentUser().getUid();
                            saveUserSession(userId);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class );
                            WriteSharedPreferences(email);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            showToast("Login Failed. Try Again! ");
                        }
                    }
                });
    }

    private void saveUserSession(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.apply();
    }
    // showText method
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void WriteSharedPreferences(String strEmail){
        SharedPreferences sharedPreferences = getSharedPreferences("BookSpresso", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", strEmail);
        editor.apply();
    }

    private String ReadSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("BookSpresso", MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }
}