package com.example.sijar.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.response.AuthResponse
import com.example.sijar.api.model.repository.AuthRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.launch

class LoginViewModel @JvmOverloads constructor(
    private val repository: AuthRepository = AuthRepository(ApiClient.apiService)
) : ViewModel() {

    var loginState by mutableStateOf<UiState<AuthResponse>?>(null)
        private set

    fun login(kodeKelas: String, password: String) {
        viewModelScope.launch {
            loginState= UiState.Loading

            loginState = when (val result = repository.login(kodeKelas, password)) {
                is ApiResult.Success -> {
                    UiState.Success(result.data)
                }

                is ApiResult.Error -> {
                    UiState.Error(result.type)
                }
            }
        }
    }

    fun resetState() {
        loginState = null
    }
}
