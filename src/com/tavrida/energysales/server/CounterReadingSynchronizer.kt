package com.tavrida.energysales.server

import com.tavrida.energysales.data_contract.CounterReadingIdMapping
import com.tavrida.energysales.data_contract.CounterReadingSyncItem
import com.tavrida.utils.log
import kotlinx.coroutines.delay
import org.jetbrains.exposed.sql.Database

class CounterReadingSynchronizer(db: Database) {
    suspend fun sync(syncItems: List<CounterReadingSyncItem>, testMode: Boolean): List<CounterReadingIdMapping> {
        val resp = mutableListOf<CounterReadingIdMapping>()
        for (item in syncItems) {
            resp.add(CounterReadingIdMapping(item.id, item.id * 10))
        }
        "sync called".log()
        return resp
    }

}