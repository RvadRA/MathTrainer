package com.example.mathtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView questionText, feedbackText, levelText, statsText, timerText;
    private LinearLayout fruitContainer1, fruitContainer2;
    private Button option1, option2, option3, yesButton, noButton;

    private int correctAnswer;
    private boolean isYesNoQuestion;
    private boolean correctYesNoAnswer;
    private int level = 1;
    private int correctAttempts = 0;
    private int wrongAttempts = 0;

    private CountDownTimer timer;
    private long timeLeft = 15000; // 15 seconds for each level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI elements
        questionText = findViewById(R.id.questionText);
        feedbackText = findViewById(R.id.feedbackText);
        levelText = findViewById(R.id.levelText);
        statsText = findViewById(R.id.statsText);
        timerText = findViewById(R.id.timerText);
        fruitContainer1 = findViewById(R.id.fruitContainer1);
        fruitContainer2 = findViewById(R.id.fruitContainer2);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        // Button listeners
        option1.setOnClickListener(v -> checkAnswer(Integer.parseInt(option1.getText().toString())));
        option2.setOnClickListener(v -> checkAnswer(Integer.parseInt(option2.getText().toString())));
        option3.setOnClickListener(v -> checkAnswer(Integer.parseInt(option3.getText().toString())));
        yesButton.setOnClickListener(v -> checkYesNoAnswer(true));
        noButton.setOnClickListener(v -> checkYesNoAnswer(false));

        // Start the first level
        startLevel();
    }

    private void startLevel() {
        feedbackText.setText("");
        levelText.setText("Level: " + level);
        updateStats();
        generateQuestion();
        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                timerText.setText("Time: " + (timeLeft / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timerText.setText("Time's up!");
                endGame();
            }
        }.start();
    }

    private void generateQuestion() {
        Random random = new Random();
        isYesNoQuestion = random.nextBoolean(); // Randomly decide if question is Yes/No

        if (isYesNoQuestion) {
            generateYesNoQuestion();
        } else {
            generateArithmeticQuestion(random);
        }
    }

    private void generateArithmeticQuestion(Random random) {
        // Generate random counts for two types of fruits
        int fruitCount1 = random.nextInt(5) + 1;
        int fruitCount2 = random.nextInt(5) + 1;

        // Pick random fruits
        int fruitType1 = random.nextInt(3); // 0: apple, 1: banana, 2: orange
        int fruitType2 = random.nextInt(3);

        // Set images in containers
        setFruitsInContainer(fruitContainer1, fruitType1, fruitCount1);
        setFruitsInContainer(fruitContainer2, fruitType2, fruitCount2);

        // Generate the question
        questionText.setText("Сколько всего фруктов?");
        correctAnswer = fruitCount1 + fruitCount2;

        // Generate random answer options
        int wrongAnswer1 = correctAnswer + random.nextInt(3) + 1;
        int wrongAnswer2 = correctAnswer - random.nextInt(3) - 1;

        int correctPosition = random.nextInt(3);
        if (correctPosition == 0) {
            option1.setText(String.valueOf(correctAnswer));
            option2.setText(String.valueOf(wrongAnswer1));
            option3.setText(String.valueOf(wrongAnswer2));
        } else if (correctPosition == 1) {
            option1.setText(String.valueOf(wrongAnswer1));
            option2.setText(String.valueOf(correctAnswer));
            option3.setText(String.valueOf(wrongAnswer2));
        } else {
            option1.setText(String.valueOf(wrongAnswer1));
            option2.setText(String.valueOf(wrongAnswer2));
            option3.setText(String.valueOf(correctAnswer));
        }

        yesButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);
        fruitContainer1.setVisibility(View.VISIBLE); // Hide fruit container 1
        fruitContainer2.setVisibility(View.VISIBLE); // Hide fruit container 2
        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
    }

    private void generateYesNoQuestion() {
        Random random = new Random();
        int a = random.nextInt(10) + 1;
        int b = random.nextInt(10) + 1;
        int comparisonType = random.nextInt(3); // 0: <, 1: =, 2: >

        switch (comparisonType) {
            case 0:
                questionText.setText(a + " < " + b + "?");
                correctYesNoAnswer = a < b;
                break;
            case 1:
                questionText.setText(a + " = " + b + "?");
                correctYesNoAnswer = a == b;
                break;
            case 2:
                questionText.setText(a + " > " + b + "?");
                correctYesNoAnswer = a > b;
                break;
        }

        yesButton.setVisibility(View.VISIBLE);
        noButton.setVisibility(View.VISIBLE);
        option1.setVisibility(View.GONE);
        option2.setVisibility(View.GONE);
        option3.setVisibility(View.GONE);
        fruitContainer1.setVisibility(View.GONE); // Hide fruit container 1
        fruitContainer2.setVisibility(View.GONE); // Hide fruit container 2
    }

    private void setFruitsInContainer(LinearLayout container, int fruitType, int count) {
        container.removeAllViews();
        int fruitDrawable;
        switch (fruitType) {
            case 0: // Apple
                fruitDrawable = R.drawable.apple;
                break;
            case 1: // Banana
                fruitDrawable = R.drawable.banana;
                break;
            case 2: // Orange
                fruitDrawable = R.drawable.orange;
                break;
            default:
                fruitDrawable = R.drawable.apple; // Default to apple
        }

        // Add the fruit images dynamically
        for (int i = 0; i < count; i++) {
            ImageView fruit = new ImageView(this);
            fruit.setImageResource(fruitDrawable);
            fruit.setLayoutParams(new LinearLayout.LayoutParams(250, 250)); // Adjust size
            container.addView(fruit);
        }
    }

    private void checkAnswer(int userAnswer) {
        if (userAnswer == correctAnswer) {
            feedbackText.setText("Правильно!");
            correctAttempts++;
            checkLevelProgress();
        } else {
            feedbackText.setText("Неправильно! Правильный ответ: " + correctAnswer);
            wrongAttempts++;
            checkGameOver();
        }
        updateStats();
        generateQuestion();
    }

    private void checkYesNoAnswer(boolean userAnswer) {
        if (userAnswer == correctYesNoAnswer) {
            feedbackText.setText("Правильно!");
            correctAttempts++;
            checkLevelProgress();
        } else {
            feedbackText.setText("Неправильно!");
            wrongAttempts++;
            checkGameOver();
        }
        updateStats();
        generateQuestion();
    }

    private void checkLevelProgress() {
        if (correctAttempts % 2 == 0) { // Level up every 2 correct answers
            level++;
            timeLeft += 5000; // Add 5 seconds for the new level
            startLevel();
        }
    }

    private void checkGameOver() {
        if (wrongAttempts >= 3) endGame();
    }

    private void updateStats() {
        statsText.setText("Correct: " + correctAttempts + " | Wrong: " + wrongAttempts);
    }

    private void endGame() {
        if (timer != null) timer.cancel();
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("LEVEL", level);
        intent.putExtra("CORRECT_ATTEMPTS", correctAttempts);
        intent.putExtra("WRONG_ATTEMPTS", wrongAttempts);
        startActivity(intent);
        finish();
    }
}
