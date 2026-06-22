package com.github.codandotv.popcorn.domain

internal interface Logger {
    fun log(message: String)
    fun logError(message: String)
    fun logSuccess(message: String)
}
