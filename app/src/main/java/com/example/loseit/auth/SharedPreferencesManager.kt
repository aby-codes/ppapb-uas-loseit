package com.example.loseit.auth

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun setUsertype(userType: String) {
        sharedPreferences.edit().putString(KEY_USER_TYPE, userType).apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserType(): String {
        return sharedPreferences.getString(KEY_USER_TYPE, "") ?: ""
    }

    companion object {
        private const val PREF_NAME = "MyPreferences"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USER_TYPE = "userType"

        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun getInstance(context: Context): SharedPreferencesManager =
            instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
    }
}
