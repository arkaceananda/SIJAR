package com.example.sijar.viewModel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.response.Data
import com.example.sijar.api.model.repository.ProfileRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.SessionManager
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository(ApiClient.apiService)
    private val sessionManager = SessionManager.getInstance(application)

    var profileState by mutableStateOf<UiState<Data>>(UiState.Loading)
        private set

    var language by mutableStateOf(sessionManager.getLanguage())
        private set

    var isNotifEnabled by mutableStateOf(sessionManager.isNotificationEnabled())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            profileState = UiState.Loading
            val token = sessionManager.getToken()
            if (token != null) {
                when (val result = repository.getProfile("Bearer $token")) {
                    is ApiResult.Success -> {
                        val userData = result.data.data.firstOrNull()
                        if (userData != null) {
                            profileState = UiState.Success(userData)
                            sessionManager.saveUserName(userData.name)
                        } else {
                            profileState = UiState.Error(ErrorType.EmptyResponse)
                        }
                    }
                    is ApiResult.Error -> {
                        profileState = UiState.Error(result.type, result.message)
                    }
                }
            } else {
                profileState = UiState.Error(ErrorType.Unauthorized)
            }
        }
    }

    fun fetchProfile() = loadProfile()

    fun changePhoto(photoPart: MultipartBody.Part) {
        viewModelScope.launch {
            val token = sessionManager.getToken() ?: return@launch
            when (val result = repository.updatePhoto("Bearer $token", photoPart)) {
                is ApiResult.Success -> {
                    loadProfile()
                }
                is ApiResult.Error -> {
                    // Handle error, maybe show a toast via a separate UI effect flow
                }
            }
        }
    }

    fun deletePhoto() {
        val currentState = profileState
        if (currentState is UiState.Success) {
            if (currentState.data.profile != null) {
                viewModelScope.launch {
                    val token = sessionManager.getToken() ?: return@launch
                    when (val result = repository.deletePhoto("Bearer $token")) {
                        is ApiResult.Success -> {
                            loadProfile()
                        }
                        is ApiResult.Error -> {
                            // Handle error
                        }
                    }
                }
            }
        }
    }

    fun toggleNotification(enabled: Boolean) {
        sessionManager.setNotificationEnabled(enabled)
        isNotifEnabled = enabled
    }

    fun changeLanguage(lang: String) {
        sessionManager.saveLanguage(lang)
        language = lang

        val appLocale: LocaleListCompat = if (lang == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(lang)
        }
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun logout(onSuccess: () -> Unit) {
        sessionManager.clearSession()
        onSuccess()
    }
}
