package com.example.sijar.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.repository.ItemRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BarangViewModel(
    private val repository: ItemRepository = ItemRepository(ApiClient.apiService)
) : ViewModel() {

    private val _barangState = MutableStateFlow<UiState<List<Item>>>(UiState.Loading)
    val barangState: StateFlow<UiState<List<Item>>> = _barangState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

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
                val matchesQuery = item.namaItem?.contains(query, ignoreCase = true) ?: true
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

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchBarang()
            _isRefreshing.value = false
        }
    }

    private fun fetchBarang() {
        viewModelScope.launch {
            // Only set to Loading if not currently refreshing (to allow skeleton on first load)
            if (!_isRefreshing.value) {
                _barangState.value = UiState.Loading
            }
            
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
