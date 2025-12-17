package com.example.kosmvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kosmvvm.model.AppRepository
import com.example.kosmvvm.model.KamarEntity
import com.example.kosmvvm.model.PenghuniEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}

class EditKamarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val _state = MutableStateFlow(EditKamarState())
    val state: StateFlow<EditKamarState> = _state.asStateFlow()

    fun dispatch(intent: EditKamarIntent) {
        when (intent) {
            is EditKamarIntent.LoadKamar -> loadKamarById(intent.idKamar)
            is EditKamarIntent.SaveKamar -> saveKamar(intent.kamar, intent.penghuni)
            is EditKamarIntent.ResetSaveStatus -> {
                _state.update { it.copy(saveResult = null) }
            }
        }
    }

    private fun loadKamarById(idKamar: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = repository.getKamarWithPenghuniById(idKamar)

            _state.update {
                it.copy(
                    kamarData = result,
                    isLoading = false
                )
            }
        }
    }

    private fun saveKamar(kamar: KamarEntity, penghuni: PenghuniEntity) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val existingKamar = repository.getKamarByNomor(kamar.nomorKamar)

            if (existingKamar != null && existingKamar.idKamar != kamar.idKamar) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        saveResult = SaveResult.Error("Nomor kamar '${kamar.nomorKamar}' sudah digunakan!")
                    )
                }
                return@launch
            }

            repository.updateKamarDanPenghuni(kamar, penghuni)

            _state.update {
                it.copy(
                    isLoading = false,
                    saveResult = SaveResult.Success
                )
            }
        }
    }
}