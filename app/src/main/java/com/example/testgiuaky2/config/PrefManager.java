package com.example.testgiuaky2.config;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PICTURE = "picture";
    private static final String KEY_USER_ID = "userId"; // Added this key for user ID
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public PrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Updated method to include userId
    public void saveLoginDetails(String userId, String fullName, String email, String picture) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PICTURE, picture);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Helper methods to retrieve saved data
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getPicture() {
        return sharedPreferences.getString(KEY_PICTURE, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}