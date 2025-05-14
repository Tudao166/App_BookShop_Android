package com.example.testgiuaky2.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to manage SharedPreferences data
 */
public class SharedPrefManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Save user login session data
     */
    public void saveUserSession(String userId, String email, String fullName) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get logged in user's ID
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    /**
     * Get logged in user's email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    /**
     * Get logged in user's full name
     */
    public String getUserFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, null);
    }

    /**
     * Clear user data and logout
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}