package com.tavrida.energysales.api.mobile.data_contract

import kotlinx.serialization.Serializable

@Serializable
data class CounterReadingIdMapping(val id: Int, val serverId: Int)