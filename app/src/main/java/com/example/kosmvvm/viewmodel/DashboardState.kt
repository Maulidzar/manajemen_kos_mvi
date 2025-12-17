package com.example.kosmvvm.viewmodel

import com.example.kosmvvm.model.KamarWithPenghuni

data class DashboardState(
    val kamarList: List<KamarWithPenghuni> = emptyList(),
    val searchQuery: String = "",
    val filter: String = "All",
    val butuhMaintenance: Boolean = false,
    val emptyMessage: String? = null
)