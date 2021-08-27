package com.tavrida.energysales.server.settings

import java.io.File

data class BackendSettings(val port: Int, val dbDir: File, val dbName: String)
