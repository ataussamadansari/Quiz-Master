package com.example.quizmaster.Home;

import static androidx.core.app.ActivityCompat.recreate;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.quizmaster.Login_Signup.UserModel;
import com.example.quizmaster.R;
import com.example.quizmaster.ThemeHelper;
import com.example.quizmaster.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class HomeFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore database;
    UserModel userModel;
    ArrayList<UserModel> userModels = new ArrayList<>();
    CategoryModel categoryModel;
    ArrayList<CategoryModel> categoryList = new ArrayList<>();
    CategoryAdapter adapter;

    FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ThemeHelper.applyTheme(requireContext());
        // Init
        adapter = new CategoryAdapter(getContext(), categoryList);
        binding.catRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.catRV.setAdapter(adapter);

        // Instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        // Sign out and close app
        binding.proImage.setOnClickListener(v -> {
            showPopupMenu(v);
        });

        // Show category
        database.collection("categories")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error fetching categories", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    categoryList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        categoryModel = snapshot.toObject(CategoryModel.class);
                        if (categoryModel != null) {
                            categoryModel.setCategoryId(snapshot.getId());
                            categoryList.add(categoryModel);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

        // Listen for changes in users collection
        database.collection("users")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error fetching users", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userModels.clear();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        UserModel user = document.toObject(UserModel.class);
                        if (user != null) {
                            user.setUid(document.getId());
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
                                    Toast.makeText(getContext(), "Failed to update rank.", Toast.LENGTH_SHORT).show();
                                });
                    }

                    fetchUserAndUpdateProfilePic();
                });

        return view;
    }

    private void fetchUserAndUpdateProfilePic() {
        // User details show
        database.collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    userModel = documentSnapshot.toObject(UserModel.class);
                    if (userModel != null && getActivity() != null) {
                        Glide.with(getActivity())  // Ensure getActivity() is not null
                                .load(userModel.getProfile())
                                .into(binding.proImage);
                        binding.userNameTV.setText("Hi, " + userModel.getName());
                        binding.rankTV.setText(String.valueOf(userModel.getRank()));
                        binding.poinsTV.setText(String.valueOf(userModel.getPoints()));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                });
    }


    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.showProfile) {
                Toast.makeText(getContext(), "Show Profile clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.darkMode) {
                toggleDarkMode();
                return true;
            } else if (item.getItemId() == R.id.logout) {
                auth.signOut();
                Toast.makeText(getContext(), "Signed out successfully.", Toast.LENGTH_SHORT).show();
                getActivity().finish(); // Close the app
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void toggleDarkMode() {
       /* // Get the current night mode state
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            // Switch to light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(getContext(), "Switched to Light Mode", Toast.LENGTH_SHORT).show();
        } else {
            // Switch to dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(getContext(), "Switched to Dark Mode", Toast.LENGTH_SHORT).show();
        }*/

        boolean nightMode = (getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        ThemeHelper.saveTheme(getContext(), !nightMode);
        recreate(getActivity());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
