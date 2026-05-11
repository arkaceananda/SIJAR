package com.example.sijar.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.WaktuPeminjaman
import com.example.sijar.api.model.repository.WaktuRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.launch

class WaktuViewModel(application: Application) : BaseViewModel(application) {

    private val repository = WaktuRepository(ApiClient.apiService)

    var waktuState by mutableStateOf<UiState<List<WaktuPeminjaman>>>(UiState.Loading)
        private set

    init {
        fetchWaktu()
    }

    private fun fetchWaktu() {
        viewModelScope.launch {
            waktuState = UiState.Loading

            val token = getBearerToken() ?: run {
                waktuState = UiState.Error(ErrorType.Unauthorized)
                return@launch
            }

            waktuState = when (val result = repository.getWaktuPembelajaran(token)) {
                is ApiResult.Success -> UiState.Success(result.data)
                is ApiResult.Error -> UiState.Error(result.type, result.message)
            }
        }
    }
}