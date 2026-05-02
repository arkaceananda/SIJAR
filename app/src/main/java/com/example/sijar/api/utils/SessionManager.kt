package com.example.sijar.api.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "sijar_session"
        private const val KEY_TOKEN = "auth_token"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null
}
