package com.thezayin.scanner.data.repository

import com.thezayin.databases.dao.ScanResultDao
import com.thezayin.scanner.data.entitiy.toEntity
import com.thezayin.scanner.data.remote.ApiService
import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.domain.repository.ProductRepository
import io.ktor.client.HttpClient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * This class implements the [ProductRepository] interface, integrating both
 * remote data retrieval (via Ktor and the OpenFoodFacts API) and local persistence (via Room).
 *
 * @param httpClient A Ktor [HttpClient] used for making network requests.
 * @param scanResultDao A Room DAO ([ScanResultDao]) for accessing stored scan results.
 */
class ProductRepositoryImpl(
    private val apiService: ApiService, private val scanResultDao: ScanResultDao
) : ProductRepository {

    /**
     * Fetches product details from the Open Food Facts API based on the provided [barcode].
     * If the product is found, it constructs a [ResultScreenItem] with the relevant
     * information (name, imageUrl, brands, etc.). If not found or on any error, returns
     * a [Result.Failure].
     *
     * @param barcode The barcode string used to query the Open Food Facts API.
     * @return A [Result.Success] containing a [ResultScreenItem] if found, or a [Result.Failure] otherwise.
     */
    override suspend fun fetchProductDetails(barcode: String): Result<ResultScreenItem> {
        return try {
            val productResponse: JsonObject? = apiService.fetchProductDetails(barcode)
            if (productResponse != null) {
                val product = productResponse["product"]?.jsonObject
                if (product != null) {
                    val name = product["product_name"]?.jsonPrimitive?.contentOrNull
                    val imageUrl = product["image_url"]?.jsonPrimitive?.contentOrNull
                    val brands = product["brands"]?.jsonPrimitive?.contentOrNull
                    val links = mapOf(
                        "amazon" to product["amazon_url"]?.jsonPrimitive?.contentOrNull,
                        "open_food" to product["open_food_url"]?.jsonPrimitive?.contentOrNull,
                        "ebay" to product["ebay_url"]?.jsonPrimitive?.contentOrNull
                    ).filterValues { !it.isNullOrEmpty() }
                    val result = ResultScreenItem(
                        name = name ?: "",
                        imageUrl = imageUrl ?: "",
                        brands = brands ?: "",
                        links = links,
                        type = "Barcode",
                        imageUri = "",
                        result = barcode,
                        timestamp = System.currentTimeMillis(),
                        productFound = true,
                        isFavorite = false,
                    )
                    Result.Success(result)
                } else {
                    Result.Failure(Exception("Product not found"))
                }
            } else {
                Result.Failure(Exception("HTTP error ${productResponse}"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * Inserts the given [product] into the local Room database and returns a new
     * [ResultScreenItem] that includes the auto-generated ID from the database.
     * This ensures any future updates to the product (e.g., toggling favorite) can
     * correctly match the stored record.
     *
     * @param product The [ResultScreenItem] object to be inserted into the database.
     * @return A [Result.Success] containing the updated [ResultScreenItem] with a valid ID,
     *         or a [Result.Failure] if the insertion fails.
     */
    override suspend fun addToDb(product: ResultScreenItem): Result<ResultScreenItem> {
        return try {
            val entity = product.toEntity()
            val generatedId = scanResultDao.insert(entity)
            val insertedEntity = scanResultDao.getAll().find { it.id == generatedId }
            if (insertedEntity != null) {
                val updatedProduct = product.copy(id = insertedEntity.id)
                Result.Success(updatedProduct)
            } else {
                Result.Failure(Exception("Error retrieving inserted product"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * Updates the favorite status of the given [product] in the local Room database.
     * The [product] must have a valid id matching a record in the database.
     *
     * @param product The [ResultScreenItem] whose favorite status is to be updated.
     * @return A [Result.Success] if the update succeeds, or a [Result.Failure] otherwise.
     */
    override suspend fun updateFavorite(product: ResultScreenItem): Result<Unit> {
        return try {
            val entity = product.toEntity()
            scanResultDao.update(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
