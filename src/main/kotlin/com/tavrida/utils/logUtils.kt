package main.kotlin.com.tavrida.utils

import java.time.Instant

fun Any?.log() = println("${Instant.now()}: $this")