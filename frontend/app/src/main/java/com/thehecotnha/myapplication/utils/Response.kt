package com.thehecotnha.myapplication.utils

/**
 * A generic sealed class that represents the result of an operation.
 * @param T The type of the successful result data.
 */
sealed class Response<out T> {

        data object Idle : Response<Nothing>()

        data object Loading : Response<Nothing>()

        data class Success<out T>(
            val data: T?
        ) : Response<T>()

        data class Failure(
            val e: Exception?
        ) : Response<Nothing>()

}