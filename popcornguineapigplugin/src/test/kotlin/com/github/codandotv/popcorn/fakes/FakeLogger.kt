package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.Logger

internal class FakeLogger(
    private val messages: MutableList<String>,
    private val errorMessages: MutableList<String>,
) : Logger {
    override fun log(message: String) {
        messages.add(message)
    }

    override fun logError(message: String) {
        errorMessages.add(message)
    }
}
