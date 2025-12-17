package com.example.kosmvvm.viewmodel

import com.example.kosmvvm.model.KamarEntity
import com.example.kosmvvm.model.PenghuniEntity

sealed class DashboardIntent {
    data class SearchKamar(val query: String?) : DashboardIntent()
    data class ApplyFilter(val filterType: String, val maintenance: Boolean) : DashboardIntent()
    data class DeleteKamar(val kamar: KamarEntity, val penghuni: PenghuniEntity?) : DashboardIntent()
}