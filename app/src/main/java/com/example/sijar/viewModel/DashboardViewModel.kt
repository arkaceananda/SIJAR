package com.example.sijar.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.UiState
import com.example.sijar.api.model.data.DashboardData
import com.example.sijar.api.model.repository.DashboardRepository
import com.example.sijar.api.utils.ApiResult
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : BaseViewModel(application) {
    private val repository: DashboardRepository = DashboardRepository(ApiClient.apiService)

    var dashboardState by mutableStateOf<UiState<DashboardData>>(UiState.Loading)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    val userName: String get() = sessionManager.getUserKode()

    init {
        fetchDashboard()
    }

    private fun fetchDashboard() {
        viewModelScope.launch {
            dashboardState = UiState.Loading
            dashboardState = when (val result = repository.getDashboardData()) {
                is ApiResult.Success -> UiState.Success(result.data.data)
                is ApiResult.Error -> UiState.Error(result.type)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            fetchDashboard()
            isRefreshing = false
        }
    }
}
