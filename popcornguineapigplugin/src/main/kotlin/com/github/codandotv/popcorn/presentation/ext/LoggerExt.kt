package com.github.codandotv.popcorn.presentation.ext

import org.gradle.api.logging.Logger

private const val LOGGER_PREFIX = "\uD83C\uDF7F\uD83D\uDC39"

private const val RED_COLOR = "\u001B[31m"
private const val GREEN_COLOR = "\u001B[32m"
private const val YELLOW_COLOR = "\u001B[33m"
private const val RESET_COLOR = "\u001B[0m"

fun Logger.popcornLoggerLifecycle(message: String) {
    lifecycle(
        LOGGER_PREFIX + GREEN_COLOR + message + RESET_COLOR
    )
}

fun Logger.popcornLoggerInfo(message: String) {
    info(
        LOGGER_PREFIX + message
    )
}

fun Logger.popcornLoggerWarn(message: String) {
    warn(
        LOGGER_PREFIX + YELLOW_COLOR + message + RESET_COLOR
    )
}

fun Logger.popcornLoggerError(message: String) {
    error(
        LOGGER_PREFIX + RED_COLOR + message + RESET_COLOR
    )
}

fun Logger.popcornLoggerDebug(message: String) {
    debug(
        LOGGER_PREFIX + YELLOW_COLOR + message + RESET_COLOR
    )
}
