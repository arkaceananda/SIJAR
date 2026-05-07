package com.example.sijar.viewModel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.repository.ItemRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BarangViewModel(
    private val repository: ItemRepository = ItemRepository(ApiClient.apiService)
) : ViewModel() {

    var barangState by mutableStateOf<UiState<List<Item>>>(UiState.Idle)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    /* FOR UI */
    var searchQuery by mutableStateOf("")
        private set
    
    /* FOR DEBOUNCE */
    private var debouncedQuery by mutableStateOf("")

    var selectedJurusan by mutableStateOf<Int?>(null)
        private set

    val filteredBarang: List<Item>
        get() = derivedStateOf {
            val state = barangState
            val query = debouncedQuery
            val jurusanId = selectedJurusan

            if (state is UiState.Success) {
                state.data.filter { item ->
                    val matchesQuery = item.namaItem?.contains(query, ignoreCase = true) ?: true
                    val matchesJurusan = if (jurusanId != null) item.kategoriJurusanId == jurusanId else true
                    matchesQuery && matchesJurusan
                }
            } else {
                emptyList()
            }
        }.value

    init {
        fetchBarang()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            fetchBarang()
            isRefreshing = false
        }
    }

    private fun fetchBarang() {
        viewModelScope.launch {
            if (!isRefreshing) {
                barangState = UiState.Loading
            }
            val result = repository.getItems(selectedJurusan, debouncedQuery)

            barangState = when (result) {
                is ApiResult.Success -> {
                    val listBarang = result.data.paginator.barangList
                    UiState.Success(listBarang)
                }

                is ApiResult.Error -> {
                    UiState.Error(result.type)
                }
            }
        }
    }

    private var searchJob: Job? = null
    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(300L)
            debouncedQuery = newQuery
            fetchBarang()
        }
    }

    fun onJurusanSelected(jurusanId: Int?) {
        selectedJurusan = jurusanId
        fetchBarang()
    }
}
