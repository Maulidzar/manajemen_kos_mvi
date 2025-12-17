package com.example.kosmvvm.viewmodel

import com.example.kosmvvm.model.KamarWithPenghuni

data class EditKamarState(
    val kamarData: KamarWithPenghuni? = null,
    val isLoading: Boolean = false,
    val saveResult: SaveResult? = null
)