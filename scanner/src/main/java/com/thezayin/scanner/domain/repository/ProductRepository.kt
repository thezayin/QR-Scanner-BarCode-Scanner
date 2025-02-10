package com.thezayin.scanner.domain.repository

import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.model.ResultScreenItem

/**
 * Defines a contract for product-related data operations, combining both local (database) and
 * remote (API) interactions. This abstraction allows the domain layer to remain independent
 * of specific data sources or implementations.
 */
interface ProductRepository {

    /**
     * Fetches detailed product information for the given [barcode] from a remote source
     * (for instance, the Open Food Facts API).
     *
     * @param barcode The product identifier or code used to query the remote service.
     * @return A [Result.Success] containing a [ResultScreenItem] if the product is found,
     *         or a [Result.Failure] if there is an error or the product is unavailable.
     */
    suspend fun fetchProductDetails(barcode: String): Result<ResultScreenItem>

    /**
     * Inserts the given [product] into the local database and returns a new [ResultScreenItem]
     * with its auto-generated ID from the database. This ensures future updates match the
     * correct record.
     *
     * @param product A [ResultScreenItem] representing the scanned or created product data.
     * @return A [Result.Success] containing the updated [ResultScreenItem] with a valid ID,
     *         or a [Result.Failure] if insertion fails.
     */
    suspend fun addToDb(product: ResultScreenItem): Result<ResultScreenItem>

    /**
     * Updates the favorite status of the given [product] in the local database. The [product]
     * must already contain a valid database id to be matched correctly.
     *
     * @param product The [ResultScreenItem] whose favorite status (or other fields) needs updating.
     * @return A [Result.Success] if the update is successful, or a [Result.Failure] if it fails.
     */
    suspend fun updateFavorite(product: ResultScreenItem): Result<Unit>
}
