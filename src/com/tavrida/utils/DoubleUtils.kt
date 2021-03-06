package com.tavrida.utils


operator fun Double?.minus(other: Double?): Double? {
    return (this ?: return null) - (other ?: return null)
}

operator fun Double?.plus(other: Double?): Double? {
    return (this ?: return null) + (other ?: return null)
}

operator fun Double?.times(other: Double?): Double? {
    return (this ?: return null) * (other ?: return null)
}

operator fun Double?.times(other: Double): Double? {
    return (this ?: return null) * other
}

operator fun Double?.times(other: Int): Double? {
    return (this ?: return null) * other
}

operator fun Double?.div(other: Double?): Double? {
    return (this ?: return null) / (other ?: return null)
}

operator fun Double?.div(other: Double): Double? {
    return (this ?: return null) / other
}

operator fun Double.div(other: Double?): Double? {
    return this / (other ?: return null)
}

inline fun Double?.orZero() = orDefault(0.0)
inline fun Double?.orDefault(default: Double = 0.0) = this ?: default

fun Double?.noTrailingZero() = this?.noTrailingZero().orEmpty()

fun Double.noTrailingZero(): String {
    val str = toStringNoExponent()
    return if (str.at(-1) == '0' && str.at(-2) == '.') {
        str.substring(0, str.length - 2)
    } else
        str
}

fun Double.toStringNoExponent(): String = toBigDecimal().toPlainString()

private fun String.at(index: Int): Char {
    if (index < 0)
        return this[this.length + index]
    return this[index]
}