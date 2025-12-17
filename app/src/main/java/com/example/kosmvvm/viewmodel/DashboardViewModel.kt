package com.example.kosmvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.kosmvvm.model.AppRepository
import com.example.kosmvvm.model.KamarWithPenghuni
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()
    private var allKamarRaw: List<KamarWithPenghuni> = emptyList()

    private val dbObserver = Observer<List<KamarWithPenghuni>> { newData ->
        allKamarRaw = newData
        processFiltering()
    }

    init {
        repository.getAllKamarWithPenghuni().observeForever(dbObserver)
    }

    fun dispatch(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.SearchKamar -> {
                _state.update { it.copy(searchQuery = intent.query ?: "") }
                processFiltering()
            }
            is DashboardIntent.ApplyFilter -> {
                _state.update { it.copy(filter = intent.filterType, butuhMaintenance = intent.maintenance) }
                processFiltering()
            }
            is DashboardIntent.DeleteKamar -> {
                deleteKamar(intent.kamar, intent.penghuni)
            }
        }
    }

    private fun processFiltering() {
        val currentState = _state.value
        var result = allKamarRaw

        when (currentState.filter) {
            "Kosong" -> result = result.filter { !it.kamar.statusTerisi }
            "Belum Bayar" -> result = result.filter {
                it.kamar.statusTerisi && it.kamar.statusBayar == false
            }
        }

        if (currentState.butuhMaintenance) {
            result = result.filter { !it.kamar.statusMaintenance.isNullOrBlank() }
        }

        if (currentState.searchQuery.isNotEmpty()) {
            val query = currentState.searchQuery
            result = result.filter { data ->
                val kamarNomorMatch = data.kamar.nomorKamar.contains(query, ignoreCase = true)
                val kamarDisplayMatch = "Kamar ${data.kamar.nomorKamar}".contains(query, ignoreCase = true)
                val penghuniMatch = data.penghuni?.namaPenghuni?.contains(query, ignoreCase = true) == true
                kamarNomorMatch || kamarDisplayMatch || penghuniMatch
            }
        }

        val message = if (result.isEmpty() && allKamarRaw.isNotEmpty()) {
            "Tidak menemukan kamar yang sesuai"
        } else if (allKamarRaw.isEmpty()) {
            "Belum ada data kamar"
        } else {
            null
        }

        _state.update {
            it.copy(
                kamarList = result,
                emptyMessage = message
            )
        }
    }

    private fun deleteKamar(kamar: com.example.kosmvvm.model.KamarEntity, penghuni: com.example.kosmvvm.model.PenghuniEntity?) {
        viewModelScope.launch {
            repository.deleteKamarDanPenghuni(kamar, penghuni)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.getAllKamarWithPenghuni().removeObserver(dbObserver)
    }
}