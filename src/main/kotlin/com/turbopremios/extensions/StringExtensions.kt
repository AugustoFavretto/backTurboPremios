package com.turbopremios.extensions

fun String.onlyDigits(): String {
    return replace(Regex("\\D"), "")
}