package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.Logger

internal class FakeLogger(
    internal val logMessages: MutableList<String>,
    internal val successMessages: MutableList<String>,
    internal val errorMessages: MutableList<String>,
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
