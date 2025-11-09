package com.example.playit.core

/**
 * Sealed class for handling operation results
 * Used throughout the app for better error handling
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    val isLoading: Boolean
        get() = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }
    
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Throwable) -> R,
        onLoading: () -> R = { throw IllegalStateException("Unexpected loading state") }
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception)
        is Loading -> onLoading()
    }
}

