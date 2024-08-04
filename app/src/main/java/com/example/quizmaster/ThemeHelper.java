package com.example.quizmaster;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private static final String P_FILE = "theme_pref";
    private static final String P_MODE = "night_mode";

    public static void applyTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(P_FILE, Context.MODE_PRIVATE);
        boolean nightMode = sharedPreferences.getBoolean(P_MODE, false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void saveTheme(Context context, boolean nightMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(P_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(P_MODE, nightMode);
        editor.apply();
    }

}
