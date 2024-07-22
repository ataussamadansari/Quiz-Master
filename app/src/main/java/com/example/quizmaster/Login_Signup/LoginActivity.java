package com.example.quizmaster.Login_Signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizmaster.MainActivity;
import com.example.quizmaster.R;
import com.example.quizmaster.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    Boolean aBoolean = true;
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    String name, phone, email, pass;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //authentication
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();


        //already login
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }


        binding.lTV.setText("Login");
        binding.signUpCard.setVisibility(View.GONE);

        binding.btnSign.setOnClickListener(v -> {
            binding.signUpCard.setVisibility(View.VISIBLE);
            binding.logInCard.setVisibility(View.GONE);
            binding.sTV.setText("SignUp");
        });

        binding.btnLogin.setOnClickListener(v -> {
            binding.signUpCard.setVisibility(View.GONE);
            binding.logInCard.setVisibility(View.VISIBLE);
            binding.lTV.setText("LogIn");
        });

        //Login
        binding.loginBtn.setOnClickListener(v -> {
            String email, password;
            email = binding.userNameET.getText().toString();
            password = binding.passwordET.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill Email & Password", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //SignUp
        binding.signUpBtn.setOnClickListener(v -> {
            name = binding.nameET.getText().toString();
            phone = binding.phoneET.getText().toString();
            email = binding.emailET.getText().toString();
            pass = binding.passET.getText().toString();


            /*if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
            } else {*/
            UserModel user = new UserModel(name, email, phone, pass, "https://firebasestorage.googleapis.com/v0/b/quiz-master-70e80.appspot.com/o/avtar6.jpg?alt=media&token=343709a2-b6e1-4834-b61a-593d8ad66fd7", 0, 0);

            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();
                    database.collection("users")
                            .document(uid)
                            .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    } else {
                                        Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    Toast.makeText(LoginActivity.this, "Successfully SignUp", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

//            }
        });
    }
}