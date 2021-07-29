package com.tavrida.utils

import kotlin.math.round

inline fun Double.round1() = round(this * 100) / 100
inline fun Double.round2() = round(this * 100) / 100
inline fun Double.round3() = round(this * 1000) / 1000
