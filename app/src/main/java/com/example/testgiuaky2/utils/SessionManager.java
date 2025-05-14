package com.example.testgiuaky2.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String userId, String email, String fullName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.apply();
    }

    /**
     * Get stored session data
     */
    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, null);
    }

    /**
     * Check login status
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clear session details
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}