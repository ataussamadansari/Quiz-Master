package com.example.adminquizmaster;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etAnswer;
    private FirebaseFirestore database;
    private List<String> categoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseApp.initializeApp(this);

        database = FirebaseFirestore.getInstance();

        spinnerCategory = findViewById(R.id.spinnerCategory);
        etQuestion = findViewById(R.id.etQuestion);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);
        etAnswer = findViewById(R.id.etAnswer);

        loadCategories();

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitQuestion();
            }
        });
    }

    private void loadCategories() {
        database.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> categoryNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            categoryNames.add(document.getString("categoryName"));
                            categoryIds.add(document.getId());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitQuestion() {
        String question = etQuestion.getText().toString().trim();
        String optionA = etOptionA.getText().toString().trim();
        String optionB = etOptionB.getText().toString().trim();
        String optionC = etOptionC.getText().toString().trim();
        String optionD = etOptionD.getText().toString().trim();
        String answer = etAnswer.getText().toString().trim();

        if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                optionC.isEmpty() || optionD.isEmpty() || answer.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedCategoryId = categoryIds.get(spinnerCategory.getSelectedItemPosition());

        database.collection("categories")
                .document(selectedCategoryId)
                .collection("questions")
                .orderBy("index")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int newIndex = 0;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        newIndex = queryDocumentSnapshots.size();
                    }

                    OptionModel optionModel = new OptionModel(question, optionA, optionB, optionC, optionD, answer, newIndex + 1);
                    database.collection("categories")
                            .document(selectedCategoryId)
                            .collection("questions")
                            .add(optionModel)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(MainActivity.this, "Question added successfully", Toast.LENGTH_SHORT).show();
                                clearFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Failed to add question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to fetch questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        etQuestion.setText("");
        etOptionA.setText("");
        etOptionB.setText("");
        etOptionC.setText("");
        etOptionD.setText("");
        etAnswer.setText("");
    }
}
