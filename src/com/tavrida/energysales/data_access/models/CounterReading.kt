package com.tavrida.energysales.data_access.models

import java.time.LocalDateTime

class CounterReading(
    var id: Int,
    val counterId: Int,
    val reading: Double,
    val readingTime: LocalDateTime,
    val user: String,
    val comment: String?,
    val synchronized: Boolean,
    val syncTime: LocalDateTime?,
    val serverId: Int?
)