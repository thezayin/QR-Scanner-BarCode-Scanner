package com.thezayin.scanner.domain.usecase

import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.domain.repository.ProductRepository

/**
 * A use case that handles adding a scanned product (represented by [ResultScreenItem]) to
 * the local database. Upon successful insertion, an updated [ResultScreenItem] (with the
 * auto-generated ID from the database) is returned.
 *
 * @property repository The [ProductRepository] instance that performs the actual data operations.
 */
class AddProductToDbUseCase(private val repository: ProductRepository) {

    /**
     * Executes the process of adding [product] to the local database. Returns a [Result.Success]
     * containing the newly inserted [ResultScreenItem] (complete with its assigned ID), or a
     * [Result.Failure] if any error occurs during insertion.
     *
     * @param product The scanned product or item to be saved to the database.
     * @return A [Result] wrapping either the updated [ResultScreenItem] or an exception.
     */
    suspend fun execute(product: ResultScreenItem): Result<ResultScreenItem> {
        return repository.addToDb(product)
    }
}