package com.example.mathtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get data from MainActivity
        int level = getIntent().getIntExtra("LEVEL", 1);
        int correctAttempts = getIntent().getIntExtra("CORRECT_ATTEMPTS", 0);
        int wrongAttempts = getIntent().getIntExtra("WRONG_ATTEMPTS", 0);

        // Calculate the score
        int score = (level * 10) + (correctAttempts * 5) - (wrongAttempts * 2);

        // Bind UI elements
        TextView resultText = findViewById(R.id.resultText);
        TextView statsText = findViewById(R.id.statsText);
        TextView highScoresText = findViewById(R.id.highScoresText);
        ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);

        Button restartButton = findViewById(R.id.restartButton);
        Button exitButton = findViewById(R.id.exitButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set result and statistics
        resultText.setText("Game Over!");
        statsText.setText("Level Reached: " + level + "\nCorrect Answers: " + correctAttempts + "\nWrong Answers: " + wrongAttempts);

        // Show ProgressBar while fetching high scores
        loadingIndicator.setVisibility(View.VISIBLE);
        highScoresText.setText("Fetching high scores...");

        // Save the user's high score and fetch updated scores
        saveHighScore(score, highScoresText, loadingIndicator);

        // Restart Game button functionality
        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Exit Game button functionality
        exitButton.setOnClickListener(v -> finish());

        // Log Out button functionality
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Log out user
            navigateToLoginActivity();
        });
    }

    // Navigate to LoginActivity
    private void navigateToLoginActivity() {
        Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Save high score to Firebase and update immediately
    private void saveHighScore(int newScore, TextView highScoresText, ProgressBar loadingIndicator) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();

            if (email != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HighScores");

                // Check if the user's score already exists and compare
                databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        HighScore existingHighScore = snapshot.getValue(HighScore.class);

                        if (existingHighScore == null || newScore > existingHighScore.getScore()) {
                            // Update score if it's higher or doesn't exist
                            HighScore highScore = new HighScore(userId, email, newScore);
                            databaseReference.child(userId).setValue(highScore)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Immediately fetch and display updated high scores
                                            fetchHighScores(highScoresText, loadingIndicator);
                                        } else {
                                            Toast.makeText(ResultActivity.this, "Failed to save score!", Toast.LENGTH_SHORT).show();
                                            loadingIndicator.setVisibility(View.GONE); // Hide ProgressBar on failure
                                        }
                                    });
                        } else {
                            // Fetch scores to ensure the latest are displayed
                            fetchHighScores(highScoresText, loadingIndicator);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(ResultActivity.this, "Failed to check existing score!", Toast.LENGTH_SHORT).show();
                        loadingIndicator.setVisibility(View.GONE); // Hide ProgressBar on failure
                    }
                });
            }
        }
    }

    // Fetch top high scores from Firebase
    private void fetchHighScores(TextView highScoresText, ProgressBar loadingIndicator) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HighScores");

        databaseReference.orderByChild("score").limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<HighScore> highScores = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HighScore score = snapshot.getValue(HighScore.class);
                    if (score != null) {
                        highScores.add(score);
                    }
                }

                // Sort the high scores in descending order
                Collections.reverse(highScores);
                displayHighScores(highScores, highScoresText);

                // Hide the ProgressBar after data is fetched
                loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ResultActivity.this, "Failed to fetch high scores!", Toast.LENGTH_SHORT).show();
                loadingIndicator.setVisibility(View.GONE); // Hide ProgressBar on failure
            }
        });
    }

    // Display high scores in the UI
    private void displayHighScores(List<HighScore> highScores, TextView highScoresText) {
        if (highScores.isEmpty()) {
            highScoresText.setText("No scores available.");
            return;
        }

        StringBuilder highScoresBuilder = new StringBuilder("Top Scores:\n");
        for (int i = 0; i < highScores.size(); i++) {
            HighScore score = highScores.get(i);

            // Remove the domain from the email
            String email = score.getEmail();
            if (email != null && email.contains("@")) {
                email = email.substring(0, email.indexOf("@"));
            }

            highScoresBuilder.append(i + 1).append(". ")
                    .append(email).append(" - ")
                    .append(score.getScore()).append("\n");
        }

        highScoresText.setText(highScoresBuilder.toString());
    }
}
