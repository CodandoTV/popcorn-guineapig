package com.gabrielbmoro.popcorn.domain.entity

sealed class CheckResult {
    object Success : CheckResult()

    data class Failure(val errorMessage: String) : CheckResult()
}