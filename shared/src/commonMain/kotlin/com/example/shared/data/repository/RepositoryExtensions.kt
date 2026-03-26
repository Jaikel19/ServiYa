package com.example.shared.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * Catches exceptions on a Flow<List<T>> and emits an empty list.
 */
fun <T> Flow<List<T>>.catchEmpty(tag: String): Flow<List<T>> =
    catch { e ->
        println("ERROR $tag: ${e.message}")
        emit(emptyList())
    }

/**
 * Catches exceptions on a Flow<T?> and emits null.
 */
fun <T> Flow<T?>.catchNull(tag: String): Flow<T?> =
    catch { e ->
        println("ERROR $tag: ${e.message}")
        emit(null)
    }

/**
 * Wraps a suspend call returning a nullable value, catching exceptions and returning null.
 */
inline fun <T> safeNullableCall(tag: String, block: () -> T?): T? =
    try {
        block()
    } catch (e: Exception) {
        println("ERROR $tag: ${e.message}")
        null
    }

/**
 * Wraps a suspend call returning a String ID, catching exceptions and returning "".
 */
inline fun safeStringCall(tag: String, block: () -> String): String =
    try {
        block()
    } catch (e: Exception) {
        println("ERROR $tag: ${e.message}")
        ""
    }

/**
 * Wraps a suspend call returning Unit, catching exceptions silently.
 */
inline fun safeUnitCall(tag: String, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        println("ERROR $tag: ${e.message}")
    }
}
