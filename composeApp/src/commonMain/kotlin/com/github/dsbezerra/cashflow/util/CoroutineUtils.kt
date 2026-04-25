package com.github.dsbezerra.cashflow.util

import kotlinx.coroutines.CancellationException

inline fun <T> safeRunCatching(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
