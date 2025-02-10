package com.thezayin.scanner.domain.usecase

import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.domain.repository.ProductRepository

/**
 * A use case that manages the process of updating the favorite status of a given [ResultScreenItem]
 * in the local database.
 *
 * @property repository The [ProductRepository] that provides access to local data storage operations.
 */
class UpdateFavoriteUseCase(private val repository: ProductRepository) {

    /**
     * Executes the update of the specified [product]'s favorite status in the local database.
     *
     * @param product The [ResultScreenItem] to be updated, which should contain the correct database id.
     * @return A [Result.Success] if the update was performed successfully, or a [Result.Failure] otherwise.
     */
    suspend fun execute(product: ResultScreenItem): Result<Unit> {
        return repository.updateFavorite(product)
    }
}
