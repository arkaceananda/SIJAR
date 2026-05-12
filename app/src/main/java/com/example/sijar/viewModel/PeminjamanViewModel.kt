package com.example.sijar.viewModel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.sijar.R
import com.example.sijar.api.model.data.Peminjaman
import com.example.sijar.api.model.data.WaktuPeminjaman
import com.example.sijar.api.model.data.response.CreatePeminjamanResponse
import com.example.sijar.api.model.repository.PeminjamanRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.api.utils.ApiResult
import com.example.sijar.api.utils.ErrorType
import com.example.sijar.api.utils.NotificationScheduler
import com.example.sijar.api.utils.UiState
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class PeminjamanViewModel(application: Application) : BaseViewModel(application) {

    private val repository = PeminjamanRepository(ApiClient.apiService)

    var listState by mutableStateOf<UiState<List<Peminjaman>>>(UiState.Idle)
        private set
    var submitState by mutableStateOf<UiState<CreatePeminjamanResponse>>(UiState.Idle)
        private set
    var isRefreshing by mutableStateOf(false)
        private set

    var selectedJamMulai by mutableStateOf<WaktuPeminjaman?>(null)
        private set

    var selectedJamSelesai by mutableStateOf<WaktuPeminjaman?>(null)
        private set

    var keperluan by mutableStateOf("")
        private set
    var selectedItemId by mutableStateOf<Int?>(null)
        private set
    var kodeUnit by mutableStateOf("")
        private set

    private var _cachedWaktu = listOf<WaktuPeminjaman>()
    val selectedWaktuIds: List<String>
        get() {
            val mulai = selectedJamMulai ?: return emptyList()
            val selesai = selectedJamSelesai ?: return emptyList()
            if (selesai.jamKe < mulai.jamKe) return emptyList()

            return _cachedWaktu
                .filter { it.jamKe in mulai.jamKe..selesai.jamKe }
                .map { waktu ->
                    JSONObject().apply {
                        put("jam_ke", waktu.jamKe)
                        put("start_time", waktu.startTime)
                        put("end_time", waktu.endTime)
                    }.toString()
                }
        }
    var selectedBuktiFoto by mutableStateOf<File?>(null)
        private set

    val peminjamanSelesai: List<Peminjaman>
        get() = (listState as? UiState.Success)?.data
            ?.filter {
                it.statusPinjaman == "selesai" ||
                        it.statusTujuan?.lowercase() == "rejected"
            }
            ?.sortedByDescending { it.createdAt }
            ?: emptyList()

    val peminjamanActive: List<Peminjaman>
        get() = (listState as? UiState.Success)?.data
            ?.filter {
                it.statusPinjaman != "selesai" &&
                        it.statusTujuan?.lowercase() != "rejected"
            }
            ?: emptyList()

    init { fetchPeminjamanList() }

    fun fetchPeminjamanList() {
        viewModelScope.launch {
            listState = UiState.Loading
            listState = when (val result = repository.getPeminjamanList()) {
                is ApiResult.Success -> UiState.Success(result.data.paginator.list)
                is ApiResult.Error -> UiState.Error(result.type, result.message)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing = true
            fetchPeminjamanList()
            isRefreshing = false
        }
    }

    fun submitPeminjaman(context: Context) {
        if (selectedJamMulai == null || selectedJamSelesai == null) {
            submitState = UiState.Error(ErrorType.BadRequest)
            return
        }

        viewModelScope.launch {
            submitState = UiState.Loading

            when (val result = repository.createPeminjaman(
                keperluan = keperluan,
                itemId = selectedItemId!!,
                kodeUnit = kodeUnit.ifBlank { null },
                waktuIds = selectedWaktuIds,
                buktiFoto = selectedBuktiFoto
            )) {
                is ApiResult.Success -> {
                    val response = result.data
                    val peminjaman = response.data

                    submitState = UiState.Success(response)

                    if (peminjaman != null) {
                        NotificationScheduler.schedule(
                            context = context,
                            peminjamanId = peminjaman.id,
                            namaBarang = peminjaman.item?.namaItem
                                ?: context.getString(R.string.nav_item),
                            waktuDipilih = selectedWaktuIds.mapNotNull { json ->
                                runCatching {
                                    val obj = JSONObject(json)
                                    WaktuPeminjaman(
                                        jamKe = obj.getInt("jam_ke"),
                                        startTime = obj.getString("start_time"),
                                        endTime = obj.getString("end_time")
                                    )
                                }.getOrNull()
                            }
                        )
                    }
                }
                is ApiResult.Error -> {
                    submitState = UiState.Error(result.type, result.message)
                }
            }
        }
    }

    fun onKeperluanChange(value: String) { keperluan = value }
    fun onItemSelected(id: Int) { selectedItemId = id }
    fun onKodeUnitChange(value: String) { kodeUnit = value }
    fun onBuktiFotoSelected(file: File) { selectedBuktiFoto = file }

    fun setCachedWaktu(waktuList: List<WaktuPeminjaman>) {
        _cachedWaktu = waktuList
    }

    fun onJamMulaiSelected(waktu: WaktuPeminjaman) {
        selectedJamMulai = waktu
        if (selectedJamSelesai != null && selectedJamSelesai!!.jamKe < waktu.jamKe) {
            selectedJamSelesai = null
        }
    }

    fun onJamSelesaiSelected(waktu: WaktuPeminjaman) {
        selectedJamSelesai = waktu
    }

    fun resetSubmitState() { submitState = UiState.Idle }

    fun resetForm() {
        keperluan = ""
        selectedItemId = null
        kodeUnit = ""
        selectedJamMulai = null
        selectedJamSelesai = null
        selectedBuktiFoto = null
        submitState = UiState.Idle
    }
}