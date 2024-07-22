package com.example.quizmaster.Profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.quizmaster.Login_Signup.UserModel;
import com.example.quizmaster.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseFirestore database;
    FirebaseAuth auth;
    UserModel userModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

// profile set
        database.collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userModel = documentSnapshot.toObject(UserModel.class);

                        Glide.with(getContext()).load(userModel.getProfile()).into(binding.proImg);
                        assert userModel != null;
                        binding.userName.setText(String.valueOf(userModel.getName()));
                        binding.userEmail.setText(String.valueOf(userModel.getEmail()));
                        binding.userPhone.setText(String.valueOf(userModel.getPhone()));
                        binding.userPass.setText(String.valueOf(userModel.getPassword()));
                        binding.userRank.setText("Rank: "+ userModel.getRank() + "\nPoints: " + userModel.getPoints());

                    }
                });

        return view;
    }
}