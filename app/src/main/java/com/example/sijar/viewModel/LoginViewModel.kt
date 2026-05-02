package com.example.sijar.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.response.AuthResponse
import com.example.sijar.api.model.repository.AuthRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(ApiClient.apiService)
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<AuthResponse>?>(null)
    val loginState: StateFlow<UiState<AuthResponse>?> = _loginState

    fun login(kodeKelas: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            
            val result = repository.login(kodeKelas, password)
            
            when (result) {
                is ApiResult.Success -> {
                    _loginState.value = UiState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _loginState.value = UiState.Error(result.message ?: "Login Gagal")
                }
            }
        }
    }

    fun resetState() {
        _loginState.value = null
    }
}
