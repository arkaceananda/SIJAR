package com.example.sijar.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.sijar.api.model.data.Item
import com.example.sijar.api.model.repository.ItemRepository
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BarangViewModel(
    application: Application,
    private val repository: ItemRepository
) : BaseViewModel(application) {
    
    private var currentPage = 1
    private var isLastPage = false
    
    val barangList = mutableStateListOf<Item>()
    
    var totalItemsFound by mutableIntStateOf(0)
        private set

    var barangState by mutableStateOf<UiState<Unit>>(UiState.Idle)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set
    
    private var debouncedQuery by mutableStateOf("")

    var selectedJurusan by mutableStateOf<Int?>(null)
        private set

    init {
        fetchBarang(isRefresh = true)
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            fetchBarang(isRefresh = true)
            isRefreshing = false
        }
    }

    fun fetchBarang(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            isLastPage = false
            barangList.clear()
        }

        if (isLastPage || (barangState is UiState.Loading && !isRefresh)) return

        viewModelScope.launch {
            if (!isRefreshing) {
                barangState = UiState.Loading
            }

            when (val result = repository.getItems(selectedJurusan, debouncedQuery, currentPage)) {
                is ApiResult.Success -> {
                    val newData = result.data.paginator.barangList
                    barangList.addAll(newData)
                    totalItemsFound = result.data.total
                    
                    isLastPage = currentPage >= result.data.paginator.lastPage
                    currentPage++
                    
                    barangState = UiState.Success(Unit)
                }
                is ApiResult.Error -> {
                    barangState = UiState.Error(result.type)
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
            fetchBarang(isRefresh = true)
        }
    }

    fun onJurusanSelected(jurusanId: Int?) {
        selectedJurusan = jurusanId
        fetchBarang(isRefresh = true)
    }
}
