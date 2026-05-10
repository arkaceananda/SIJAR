package com.example.sijar.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.WaktuPeminjaman
import com.example.sijar.api.model.repository.WaktuRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.launch

class WaktuViewModel : ViewModel() {

    private val repository = WaktuRepository(ApiClient.apiService)

    var waktuState by mutableStateOf<UiState<List<WaktuPeminjaman>>>(UiState.Loading)
        private set

    init {
        fetchWaktu()
    }

    private fun fetchWaktu() {
        viewModelScope.launch {
            waktuState = UiState.Loading
            waktuState = when (val result = repository.getWaktuPembelajaran()) {
                is ApiResult.Success -> UiState.Success(result.data)
                is ApiResult.Error -> UiState.Error(result.type, result.message)
            }
        }
    }
}