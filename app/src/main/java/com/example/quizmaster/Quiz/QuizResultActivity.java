package com.example.quizmaster.Quiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.quizmaster.Login_Signup.UserModel;
import com.example.quizmaster.R;
import com.example.quizmaster.databinding.ActivityQuizResultBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class QuizResultActivity extends AppCompatActivity {

    ActivityQuizResultBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore database;
    boolean a = true;
    UserModel userModel;

    ArrayList<UserModel> userModels = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityQuizResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check for permissions
        /*if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        }*/

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        // User image show
        database.collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
                    userModel = documentSnapshot.toObject(UserModel.class);
                    if (userModel != null) {
                        Glide.with(QuizResultActivity.this).load(userModel.getProfile()).into(binding.circleImageView);
                        updatePoints();  // Update points after setting the user model
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user details.", Toast.LENGTH_SHORT).show();
                });

        String correctCount = getIntent().getStringExtra("correctCount");
        String wrong = getIntent().getStringExtra("wrongCount");
        String missing = getIntent().getStringExtra("missingCount");
        String collectCoins = getIntent().getStringExtra("collectCoins");
        String queSize = getIntent().getStringExtra("queSize");

        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.score.setText(correctCount);
        binding.m.setText("M: " + missing);
        binding.w.setText("W: " + wrong);
        binding.collectCoins.setText(collectCoins);
        binding.textView10.setText("/ " + queSize);

        binding.toastBtn.setOnClickListener(v -> {
            if (a) {
                binding.toast.setVisibility(View.VISIBLE);
                a = false;
            } else {
                binding.toast.setVisibility(View.GONE);
                a = true;
            }
        });

        binding.shareBtn.setOnClickListener(v -> {
            // Implement share functionality
        });
    }

    private void updatePoints() {
        String collectCoins = getIntent().getStringExtra("collectCoins");
        if (userModel != null && collectCoins != null) {
            try {
                int collectedCoins = Integer.parseInt(collectCoins);
                int newPoints = userModel.getPoints() + collectedCoins;
                DocumentReference userRef = database.collection("users").document(Objects.requireNonNull(auth.getUid()));

                // Update points in the database
                userRef.update("points", newPoints).addOnSuccessListener(aVoid -> {
                    // Fetch all users and update ranks
                    fetchUsersAndUpdateRanks();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update points.", Toast.LENGTH_SHORT).show();
                });
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid coins value.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchUsersAndUpdateRanks() {
        database.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userModels.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserModel user = document.toObject(UserModel.class);
                            if (user != null) {
                                user.setUid(document.getId());  // Set user ID
                                userModels.add(user);
                            }
                        }

                        // Sort users by points in descending order
                        Collections.sort(userModels, (u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));

                        // Assign ranks to users based on sorted order
                        for (int i = 0; i < userModels.size(); i++) {
                            UserModel user = userModels.get(i);
                            user.setRank(i + 1);
                            database.collection("users")
                                    .document(user.getUid())
                                    .update("rank", user.getRank())
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(QuizResultActivity.this, "Failed to update rank.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(QuizResultActivity.this, "Failed to fetch users.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
