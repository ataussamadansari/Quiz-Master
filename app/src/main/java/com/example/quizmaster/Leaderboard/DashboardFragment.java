package com.example.quizmaster.Leaderboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.quizmaster.Login_Signup.UserModel;
import com.example.quizmaster.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    ArrayList<UserModel> userModels = new ArrayList<>();
    UserModel userModel;
    LeaderAdapter adapter;
    FirebaseAuth auth;
    FirebaseFirestore database;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        // Set up the RecyclerView for displaying ranks
        binding.rankRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LeaderAdapter(getContext(), userModels);
        binding.rankRV.setAdapter(adapter);

        // Fetch the current user's details and all users to update ranks
        fetchUsersAndUpdateRanks();

        return binding.getRoot();
    }

    private void fetchUsersAndUpdateRanks() {
        database.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userModels.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel user = document.toObject(UserModel.class);
                                userModels.add(user);
                                user.setUid(document.getId());
                            }

                            // Sort users by points in descending order
                            Collections.sort(userModels, new Comparator<UserModel>() {
                                @Override
                                public int compare(UserModel u1, UserModel u2) {
                                    return Integer.compare(u2.getPoints(), u1.getPoints());
                                }
                            });

                            // Assign ranks to users based on sorted order
                            for (int i = 0; i < userModels.size(); i++) {
                                UserModel user = userModels.get(i);
                                user.setRank(i + 1);
                                database.collection("users")
                                        .document(user.getUid())
                                        .update("rank", user.getRank());
                            }

                            // Update the RecyclerView with the new data
                            adapter.notifyDataSetChanged();

                            // Call methods to display current user and top ranks
                            fetchCurrentUserDetails();
                            displayTopRanks();

                            // Remove top 3 ranked users from the list used for the RecyclerView
                            if (userModels.size() > 3) {
                                userModels.subList(0, 3).clear();  // Remove top 3 users
                            }

                        } else {
                            // Handle the error
                        }
                    }
                });
    }


    private void fetchCurrentUserDetails() {
        database.collection("users")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        userModel = documentSnapshot.toObject(UserModel.class);

                        if (userModel != null) {
                            Glide.with(getContext()).load(userModel.getProfile()).into(binding.userImg);
                            binding.userName.setText(userModel.getName());
                            binding.userPoints.setText(String.valueOf(userModel.getPoints()));
                            binding.userRank.setText(String.valueOf(userModel.getRank()));

                            // Call to update own rank visibility
                            if (userModel.getRank() <= 3) {
                                binding.ownRank.setVisibility(View.GONE);
                            } else {
                                binding.ownRank.setVisibility(View.VISIBLE);
                                binding.rankRV.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    private void displayTopRanks() {
        if (userModels.size() > 0) {
            UserModel firstUser = userModels.get(0);
            if (firstUser != null) {
                binding.FRUrank.setText(String.valueOf(firstUser.getRank()));
                binding.FRUname.setText(firstUser.getName());
                binding.FRUpoints.setText(String.valueOf(firstUser.getPoints()));
                Glide.with(this).load(firstUser.getProfile()).into(binding.FRUimg);
            }
        }
        if (userModels.size() > 1) {
            UserModel secondUser = userModels.get(1);
            if (secondUser != null) {
                binding.SRUrank.setText(String.valueOf(secondUser.getRank()));
                binding.SRUname.setText(secondUser.getName());
                binding.SRUpoints.setText(String.valueOf(secondUser.getPoints()));
                Glide.with(this).load(secondUser.getProfile()).into(binding.SRUimg);
            }
        }
        if (userModels.size() > 2) {
            UserModel thirdUser = userModels.get(2);
            if (thirdUser != null) {
                binding.TRUrank.setText(String.valueOf(thirdUser.getRank()));
                binding.TRUname.setText(thirdUser.getName());
                binding.TRUpoints.setText(String.valueOf(thirdUser.getPoints()));
                Glide.with(this).load(thirdUser.getProfile()).into(binding.TRUimg);
            }
        }

    }
}
