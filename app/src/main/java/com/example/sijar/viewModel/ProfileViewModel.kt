package com.example.sijar.viewModel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.request.UpdatePasswordRequest
import com.example.sijar.api.model.data.response.Data
import com.example.sijar.api.model.data.response.UpdatePasswordResponse
import com.example.sijar.api.model.data.response.UpdateProfileResponse
import com.example.sijar.api.model.repository.ProfileRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class ProfileViewModel(application: Application) : BaseViewModel(application) {

    private val repository = ProfileRepository(ApiClient.apiService)

    var profileState by mutableStateOf<UiState<Data>>(UiState.Loading)
        private set
    var language by mutableStateOf(sessionManager.getLanguage())
        private set
    var isNotifEnabled by mutableStateOf(sessionManager.isNotificationEnabled())
        private set
    var changePasswordState by mutableStateOf<UiState<UpdatePasswordResponse>>(UiState.Idle)
        private set
    var updateProfileState by mutableStateOf<UiState<UpdateProfileResponse>>(UiState.Idle)
        private set

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            profileState = UiState.Loading

            val token = getBearerToken() ?: run {
                profileState = UiState.Error(ErrorType.Unauthorized)
                return@launch
            }

            when (val result = repository.getProfile(token)) {
                is ApiResult.Success -> {
                    val userData = result.data.data
                    profileState = run {
                        sessionManager.saveUserName(userData.name)
                        sessionManager.saveUserKode(userData.kode)
                        UiState.Success(userData)
                    }
                }
                is ApiResult.Error -> {
                    profileState = UiState.Error(result.type, result.message)
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
        val appLocale = if (lang == "system") {
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

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            changePasswordState = UiState.Loading

            val token = getBearerToken() ?: run {
                changePasswordState = UiState.Error(ErrorType.Unauthorized)
                return@launch
            }

            changePasswordState = when (
                val result = repository.changePassword(
                    token,
                    UpdatePasswordRequest(currentPassword, newPassword, confirmPassword)
                )
            ) {
                is ApiResult.Success -> UiState.Success(result.data)
                is ApiResult.Error -> UiState.Error(result.type, result.message)
            }
        }
    }

    fun resetChangePasswordState() { changePasswordState = UiState.Idle }

    fun updateProfile(
        name: String, 
        kode: String, 
        telepon: String?,
        removePhoto: Boolean,
        photoFile: File?
    ) {
        viewModelScope.launch {
            updateProfileState = UiState.Loading

            val token = getBearerToken() ?: run {
                updateProfileState = UiState.Error(ErrorType.Unauthorized)
                return@launch
            }

            val userId = (profileState as? UiState.Success)?.data?.id ?: run {
                updateProfileState = UiState.Error(ErrorType.Unknown)
                return@launch
            }

            updateProfileState = when (
                val result = repository.updateProfile(token, userId, name, kode, telepon, removePhoto, photoFile)
            ) {
                is ApiResult.Success -> {
                    loadProfile()
                    UiState.Success(result.data)
                }
                is ApiResult.Error -> UiState.Error(result.type, result.message)
            }
        }
    }

    fun resetUpdateProfileState() { updateProfileState = UiState.Idle }
}