package com.thezayin.scanner.domain.model

/**
 * A sealed class representing the result of an operation, typically used for QR code scanning.
 * This helps in handling success and failure scenarios in a structured way.
 *
 * @param T The type of data expected in case of a successful operation.
 */
sealed class Result<out T> {

    /**
     * Represents a successful operation, containing the resulting data.
     *
     * @param data The successful result of the operation.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation, containing the exception that caused the failure.
     *
     * @param exception The exception that was thrown during the operation.
     */
    data class Failure(val exception: Throwable) : Result<Nothing>()
}
