package com.example.mathtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Получение данных из MainActivity
        int level = getIntent().getIntExtra("LEVEL", 1);
        int correctAttempts = getIntent().getIntExtra("CORRECT_ATTEMPTS", 0);
        int wrongAttempts = getIntent().getIntExtra("WRONG_ATTEMPTS", 0);

        // Привязка элементов интерфейса
        TextView resultText = findViewById(R.id.resultText);
        TextView statsText = findViewById(R.id.statsText);
        Button restartButton = findViewById(R.id.restartButton);
        Button exitButton = findViewById(R.id.exitButton);

        // Установка текста с результатами
        resultText.setText("Game Over!");
        statsText.setText("Level Reached: " + level + "\nCorrect Answers: " + correctAttempts + "\nWrong Answers: " + wrongAttempts);

        // Обработка кнопки "Restart Game"
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Обработка кнопки "Exit Game"
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрыть активность
            }
        });
    }
}
