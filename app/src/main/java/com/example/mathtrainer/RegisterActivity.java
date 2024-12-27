package com.example.mathtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Automatically navigate to MainActivity if the user is already logged in
        firebaseAuth = FirebaseAuth.getInstance();
        // Automatically navigate to StartActivity if the user is already logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            navigateToStartActivity();
        }

        setContentView(R.layout.activity_register);

        EditText emailEditText = findViewById(R.id.registerEmailEditText);
        EditText passwordEditText = findViewById(R.id.registerPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView goToLoginTextView = findViewById(R.id.goToLoginTextView);

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    navigateToStartActivity();
                } else {
                    // Handle specific errors
                    if (task.getException() != null) {
                        String errorMessage = task.getException().getMessage();
                        if (errorMessage != null && errorMessage.contains("email address is already in use")) {
                            Toast.makeText(this, "You already have an account, please log in.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Registration Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Registration Failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });

        });

        goToLoginTextView.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void navigateToStartActivity() {
        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
        startActivity(intent);
        finish(); // Close RegisterActivity
    }
}
