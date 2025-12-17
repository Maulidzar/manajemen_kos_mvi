package com.example.kosmvvm.viewmodel

import com.example.kosmvvm.model.KamarEntity
import com.example.kosmvvm.model.PenghuniEntity

sealed class EditKamarIntent {
    data class LoadKamar(val idKamar: Int) : EditKamarIntent()
    data class SaveKamar(val kamar: KamarEntity, val penghuni: PenghuniEntity) : EditKamarIntent()
    object ResetSaveStatus : EditKamarIntent()
}