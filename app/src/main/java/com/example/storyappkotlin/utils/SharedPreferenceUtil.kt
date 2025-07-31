package com.example.storyappkotlin.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUtil(context : Context) {

    companion object {
        const val PREF_KEY = "PREF_KEY";
        const val PREF_ACCESS_TOKEN = "ACCESS_TOKEN";
        const val PREF_IS_ALREADY_HAVE_ACCOUNT = "IS_ALREADY_HAVE_ACCOUNT";
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

    private val editor: SharedPreferences.Editor = sharedPreferences.edit();

    fun isAlreadyHaveAccount(): Boolean {
        return sharedPreferences.getBoolean(PREF_IS_ALREADY_HAVE_ACCOUNT, false);
    }

    fun setAlreadyHaveAccount(value: Boolean) {
        editor.putBoolean(PREF_IS_ALREADY_HAVE_ACCOUNT, value).apply();
    }

    fun getToken(): String {
        return sharedPreferences.getString(PREF_ACCESS_TOKEN, "") ?: "";
    }

    fun setToken(token: String) {
        editor.putString(PREF_ACCESS_TOKEN, token).apply();
    }

    fun removeToken() {
        editor.remove(PREF_ACCESS_TOKEN).apply();
    }
}