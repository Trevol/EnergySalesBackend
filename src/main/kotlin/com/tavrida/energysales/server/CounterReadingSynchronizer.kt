package main.kotlin.com.tavrida.energysales.server

import main.kotlin.com.tavrida.energysales.data_contract.CounterReadingIdMapping
import main.kotlin.com.tavrida.energysales.data_contract.CounterReadingSyncItem

class CounterReadingSynchronizer() {
    suspend fun sync(syncItems: List<CounterReadingSyncItem>, testMode: Boolean): List<CounterReadingIdMapping> {
        val resp = mutableListOf<CounterReadingIdMapping>()
        for (item in syncItems) {
            resp.add(CounterReadingIdMapping(item.id, item.id * 10))
        }
        return resp
    }

}