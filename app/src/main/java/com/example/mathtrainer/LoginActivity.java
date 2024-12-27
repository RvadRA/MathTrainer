package com.example.mathtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Automatically navigate to StartActivity if the user is already logged in
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToStartActivity();
        }

        setContentView(R.layout.activity_login);

        EditText emailEditText = findViewById(R.id.loginEmailEditText);
        EditText passwordEditText = findViewById(R.id.loginPasswordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView goToRegisterTextView = findViewById(R.id.goToRegisterTextView);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    navigateToStartActivity();
                } else {
                    // Handle specific errors
                    if (task.getException() != null) {
                        String errorMessage = task.getException().getMessage();
                        if (errorMessage != null && errorMessage.contains("There is no user record")) {
                            Toast.makeText(this, "No account found with this email. Please register first.", Toast.LENGTH_LONG).show();
                        } else if (errorMessage.contains("The password is invalid")) {
                            Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Login Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Login Failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });

        });

        goToRegisterTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void navigateToStartActivity() {
        Intent intent = new Intent(LoginActivity.this, StartActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity
    }
}
