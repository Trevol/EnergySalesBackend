package com.tavrida.energysales.api.data_contract

import com.tavrida.utils.serializers.LocalDateIso8601Serializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class HelloResponse(
    val hello: String,
    @Serializable(with = LocalDateIso8601Serializer::class)
    val date: LocalDate
)