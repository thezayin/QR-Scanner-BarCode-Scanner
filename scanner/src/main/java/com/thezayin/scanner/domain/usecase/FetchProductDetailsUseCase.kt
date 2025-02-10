package com.thezayin.scanner.domain.usecase

import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.domain.repository.ProductRepository

/**
 * A use case responsible for fetching product details from a remote data source (for example,
 * the Open Food Facts API) by providing a barcode. The response is returned as a [Result],
 * wrapping either a [ResultScreenItem] if successful or an error if not.
 *
 * @property repository The [ProductRepository] that provides methods to retrieve product information.
 */
class FetchProductDetailsUseCase(private val repository: ProductRepository) {

    /**
     * Executes the process of fetching product information using the provided [barcode].
     * If the product is found, this method returns a [Result.Success] containing the
     * [ResultScreenItem]. Otherwise, it returns a [Result.Failure] with the relevant error.
     *
     * @param barcode The product's barcode to be used in the lookup.
     * @return A [Result] containing either the [ResultScreenItem] for the matching product or an error message.
     */
    suspend fun execute(barcode: String): Result<ResultScreenItem> {
        return repository.fetchProductDetails(barcode)
    }
}