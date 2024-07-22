package com.example.quizmaster;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.quizmaster.Home.HomeFragment;
import com.example.quizmaster.Leaderboard.DashboardFragment;
import com.example.quizmaster.Profile.ProfileFragment;
import com.example.quizmaster.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.navHome.setBackground(getDrawable(R.drawable.nav_btn_bg));
        binding.navDashboard.setBackground(null);
        binding.navProfile.setBackground(null);

        binding.navHome.setOnClickListener(v -> {
            binding.navHome.setBackground(getDrawable(R.drawable.nav_btn_bg));
            binding.navDashboard.setBackground(null);
            binding.navProfile.setBackground(null);
            replaceFragment(new HomeFragment());
        });

        binding.navDashboard.setOnClickListener(v -> {
            binding.navDashboard.setBackground(getDrawable(R.drawable.nav_btn_bg));
            binding.navHome.setBackground(null);
            binding.navProfile.setBackground(null);
            replaceFragment(new DashboardFragment());
        });

        binding.navProfile.setOnClickListener(v -> {
            binding.navProfile.setBackground(getDrawable(R.drawable.nav_btn_bg));
            binding.navHome.setBackground(null);
            binding.navDashboard.setBackground(null);
            replaceFragment(new ProfileFragment());
        });
        // Set initial fragment
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container);

        // Check if the current fragment is HomeFragment
        if (!(currentFragment instanceof HomeFragment)) {
            // If not, replace the fragment with HomeFragment
            replaceFragment(new HomeFragment());
            binding.navHome.setBackground(getDrawable(R.drawable.nav_btn_bg));
            binding.navDashboard.setBackground(null);
            binding.navProfile.setBackground(null);
        } else {
            // If already on HomeFragment, perform the default back action
            super.onBackPressed();
        }
    }
}