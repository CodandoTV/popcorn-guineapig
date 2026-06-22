package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.Logger

internal class FakeLogger(
    private val logMessages: MutableList<String>,
    private val successMessages: MutableList<String>,
    private val errorMessages: MutableList<String>,
) : Logger {
    override fun logSuccess(message: String) {
        successMessages.add(message)
    }

    override fun log(message: String) {
        logMessages.add(message)
    }

    override fun logError(message: String) {
        errorMessages.add(message)
    }
}
