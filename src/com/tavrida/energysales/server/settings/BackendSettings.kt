package com.tavrida.energysales.server.settings

import kotlinx.serialization.Serializable
import java.io.File

data class BackendSettings(val port: Int, val dbDir: File, val dbName: String)
