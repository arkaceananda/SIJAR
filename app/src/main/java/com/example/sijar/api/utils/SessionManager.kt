package com.example.sijar.api.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "sijar_session"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_KODE = "user_kode"
        private const val KEY_LANGUAGE = "app_language" 
        private const val KEY_NOTIF = "notif_enabled"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveToken(token: String) = prefs.edit { putString(KEY_TOKEN, token) }
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    
    fun saveUserName(name: String) = prefs.edit { putString(KEY_USER_NAME, name) }

    fun saveUserKode(kode: String) = prefs.edit { putString(KEY_USER_KODE, kode) }
    fun getUserKode(): String = prefs.getString(KEY_USER_KODE, "User") ?: "User"

    fun saveLanguage(lang: String) = prefs.edit { putString(KEY_LANGUAGE, lang) }
    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "system") ?: "system"
    
    fun setNotificationEnabled(enabled: Boolean) = prefs.edit { putBoolean(KEY_NOTIF, enabled) }
    fun isNotificationEnabled(): Boolean = prefs.getBoolean(KEY_NOTIF, true)

    fun clearSession() = prefs.edit { clear() }
    fun isLoggedIn(): Boolean = getToken() != null
}
