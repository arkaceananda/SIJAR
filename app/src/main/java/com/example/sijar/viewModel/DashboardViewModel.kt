package com.example.sijar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.UiState
import com.example.sijar.api.model.data.DashboardData
import com.example.sijar.api.model.repository.PeminjamanRepository
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PeminjamanRepository = PeminjamanRepository(ApiClient.apiService)
    private val sessionManager = SessionManager.getInstance(application)

    private val _dashboardState = MutableStateFlow<UiState<DashboardData>>(UiState.Loading)
    val dashboardState: StateFlow<UiState<DashboardData>> = _dashboardState

    init {
        fetchDashboard()
    }

    private fun fetchDashboard() {
        viewModelScope.launch {
            _dashboardState.value = UiState.Loading

            val token = sessionManager.getToken()
            if (token != null) {
                when (val result = repository.getDashboardData("Bearer $token")) {
                    is ApiResult.Success -> {
                    _dashboardState.value = UiState.Success(result.data.data)
                    }
                    is ApiResult.Error -> {
                    _dashboardState.value =
                        UiState.Error(result.message ?: "An unexpected error occurred")
                    }
                }
            } else {
                _dashboardState.value = UiState.Error("Sesi berakhir, Silahkan login kembali")
            }
        }
    }
}
