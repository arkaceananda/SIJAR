package com.example.sijar.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide.init
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.repository.ItemRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BarangViewModel(
    private val repository: ItemRepository = ItemRepository(ApiClient.apiService)
) : ViewModel() {

    private val _barangState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val barangState: StateFlow<UiState<List<Item>>> = _barangState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedJurusan = MutableStateFlow<Int?>(null)
    val selectedJurusan: StateFlow<Int?> = _selectedJurusan

    val filteredBarang: StateFlow<List<Item>> = combine(
        _barangState,
        _searchQuery,
        _selectedJurusan
    ) { state, query, jurusanId ->
        if (state is UiState.Success) {
            state.data.filter { item ->
                val matchesQuery = item.namaItem?.contains(query, ignoreCase = true)?: true
                val matchesJurusan = if (jurusanId != null) item.kategoriJurusanId == jurusanId else true
                matchesQuery && matchesJurusan
            }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchBarang()
    }

    private fun fetchBarang() {
        viewModelScope.launch {
            _barangState.value = UiState.Loading
            when (val result = repository.getItems()) {
                is ApiResult.Success -> {
                    _barangState.value = UiState.Success(result.data.data)
                }
                is ApiResult.Error -> {
                    _barangState.value = UiState.Error(result.message ?: "An unexpected error occurred")
                }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onJurusanSelected(jurusanId: Int?) {
        _selectedJurusan.value = jurusanId
    }
}
