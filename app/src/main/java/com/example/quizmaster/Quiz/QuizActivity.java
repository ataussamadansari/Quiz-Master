package com.example.quizmaster.Quiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizmaster.R;
import com.example.quizmaster.databinding.ActivityQuizBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    ActivityQuizBinding binding;
    ArrayList<QuestionModel> questionModels = new ArrayList<>();
    QuestionModel model;
    CountDownTimer countDownTimer;
    int timerValue = 20;
    int index = 0;
    int Que = 1;
    int correctCount = 0;
    int wrongCount = 0;
    int missingCount = 0;
    int collectCoins = 0;

    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnNext.setClickable(false);

        database = FirebaseFirestore.getInstance();
        String categoryId = getIntent().getStringExtra("categoryId");

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
            stopTimer();
        });

        database.collection("categories")
                .document(categoryId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    binding.catName.setText(documentSnapshot.getString("categoryName"));
                });

        fetchQuestions(categoryId);
    }

    /*private void fetchQuestions(String categoryId) {
        Random random = new Random();
        int randomIndex = random.nextInt(22);

        database.collection("categories")
                .document(categoryId)
                .collection("questions")
                .whereGreaterThanOrEqualTo("index", randomIndex)
                .orderBy("index")
                .limit(5).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() < 10) {
                        fetchRemainingQuestions(categoryId, randomIndex);
                    } else {
                        populateQuestions(queryDocumentSnapshots);
                    }
                });
    }

    private void fetchRemainingQuestions(String categoryId, int randomIndex) {
        database.collection("categories")
                .document(categoryId)
                .collection("questions")
                .whereLessThanOrEqualTo("index", randomIndex)
                .orderBy("index")
                .limit(5).get().addOnSuccessListener(this::populateQuestions);
    }

    @SuppressLint("SetTextI18n")
    private void populateQuestions(QuerySnapshot queryDocumentSnapshots) {
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
            QuestionModel questionModel = documentSnapshot.toObject(QuestionModel.class);
            questionModels.add(questionModel);
        }
        if (!questionModels.isEmpty()) {
            model = questionModels.get(index);
            setAllData();
            startTimer(timerValue);
            binding.queCounter.setText(Que + "/" + questionModels.size());
        } else {
            binding.queTV.setText("Questions not loaded.");
        }
    }*/

    private void fetchQuestions(String categoryId) {
        database.collection("categories")
                .document(categoryId)
                .collection("questions")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<QuestionModel> allQuestions = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        QuestionModel questionModel = documentSnapshot.toObject(QuestionModel.class);
                        allQuestions.add(questionModel);
                    }
                    Collections.shuffle(allQuestions);
                    questionModels = new ArrayList<>(allQuestions.subList(0, Math.min(10, allQuestions.size())));
                    if (!questionModels.isEmpty()) {
                        model = questionModels.get(index);
                        setAllData();
                        startTimer(timerValue);
                        binding.queCounter.setText(Que + "/" + questionModels.size());
                    } else {
                        binding.queTV.setText("Questions not loaded.");
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void populateQuestions(QuerySnapshot queryDocumentSnapshots) {
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
            QuestionModel questionModel = documentSnapshot.toObject(QuestionModel.class);
            questionModels.add(questionModel);
        }
        if (!questionModels.isEmpty()) {
            model = questionModels.get(index);
            setAllData();
            startTimer(timerValue);
            binding.queCounter.setText(Que + "/" + questionModels.size());
        } else {
            binding.queTV.setText("Questions not loaded.");
        }
    }

    private void startTimer(int seconds) {
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                binding.timer.setText(String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60));
            }

            @Override
            public void onFinish() {
                binding.timer.setText("00:00");
                Toast.makeText(QuizActivity.this, "Time Out", Toast.LENGTH_SHORT).show();
                missingCount++;
                moveToNextQuestion();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void resetTimer() {
        stopTimer();
        startTimer(timerValue);
    }

    private void setAllData() {
        if (model != null) {
            binding.queTV.setText(model.getQuestion());
            binding.optA.setText(model.getoA());
            binding.optB.setText(model.getoB());
            binding.optC.setText(model.getoC());
            binding.optD.setText(model.getoD());
        } else {
            binding.queTV.setText("No questions available.");
        }
    }

    private void moveToNextQuestion() {
        if (index < questionModels.size() - 1) {
            index++;
            if (Que < 10) {
                Que++;
                binding.queCounter.setText(Que + "/" + questionModels.size());
            }
            model = questionModels.get(index);
            resetColor();
            setAllData();
            resetTimer();
        } else {
            GameWon();
            stopTimer();
        }
        enableButton();
    }

    public void Correct(TextView textView) {
        textView.setBackground(getResources().getDrawable(R.drawable.right));
        binding.btnNext.setOnClickListener(v -> {
            correctCount++;
            collectCoins = collectCoins + 5;
            moveToNextQuestion();
            binding.btnNext.setClickable(false);
        });
    }

    public void Wrong(TextView textView) {
        textView.setBackground(getResources().getDrawable(R.drawable.wrong));
        binding.btnNext.setOnClickListener(v -> {
            wrongCount++;
            moveToNextQuestion();
            binding.btnNext.setClickable(false);
        });
    }

    private void GameWon() {
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra("wrongCount", String.valueOf(wrongCount));
        intent.putExtra("missingCount", String.valueOf(missingCount));
        intent.putExtra("correctCount", String.valueOf(correctCount));
        intent.putExtra("collectCoins", String.valueOf(collectCoins));
        intent.putExtra("queSize", String.valueOf(questionModels.size()));
        startActivity(intent);
        finish();
    }

    public void enableButton() {
        binding.optA.setClickable(true);
        binding.optB.setClickable(true);
        binding.optC.setClickable(true);
        binding.optD.setClickable(true);
    }

    public void disableButton() {
        binding.optA.setClickable(false);
        binding.optB.setClickable(false);
        binding.optC.setClickable(false);
        binding.optD.setClickable(false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void resetColor() {
        binding.optA.setBackground(getResources().getDrawable(R.drawable.option));
        binding.optB.setBackground(getResources().getDrawable(R.drawable.option));
        binding.optC.setBackground(getResources().getDrawable(R.drawable.option));
        binding.optD.setBackground(getResources().getDrawable(R.drawable.option));
    }

    public void OptionA(View v) {
        handleOptionClick(binding.optA, model.getoA());
    }

    public void OptionB(View v) {
        handleOptionClick(binding.optB, model.getoB());
    }

    public void OptionC(View v) {
        handleOptionClick(binding.optC, model.getoC());
    }

    public void OptionD(View v) {
        handleOptionClick(binding.optD, model.getoD());
    }

    private void handleOptionClick(TextView optionView, String selectedOption) {
        disableButton();
        binding.btnNext.setClickable(true);
        stopTimer();
        if (model != null && selectedOption.equals(model.getAns())) {
            Correct(optionView);
        } else {
            Wrong(optionView);
        }
    }

    @Override
    public void onBackPressed() {
        stopTimer();
        super.onBackPressed();
    }
}
