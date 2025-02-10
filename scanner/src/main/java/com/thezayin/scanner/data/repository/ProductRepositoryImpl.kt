package com.thezayin.scanner.data.repository

import com.thezayin.databases.dao.ScanResultDao
import com.thezayin.scanner.data.entitiy.toEntity
import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.domain.repository.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
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
    private val httpClient: HttpClient,
    private val scanResultDao: ScanResultDao
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
            val response: HttpResponse =
                httpClient.get("https://world.openfoodfacts.org/api/v0/product/$barcode.json") {
                    // Specify the type of content we expect in the response.
                    contentType(ContentType.Application.Json)
                }

            // If the response is HTTP 200 (OK), parse the JSON body for product data.
            if (response.status == HttpStatusCode.OK) {
                val responseBody = response.body<JsonObject>()
                // The "product" field in the JSON typically contains the relevant data.
                val product = responseBody["product"]?.jsonObject

                if (product != null) {
                    // Extract relevant fields from the product JSON
                    val name = product["product_name"]?.jsonPrimitive?.contentOrNull
                    val imageUrl = product["image_url"]?.jsonPrimitive?.contentOrNull
                    val brands = product["brands"]?.jsonPrimitive?.contentOrNull

                    // Extract possible links (these keys may or may not exist)
                    val links = mapOf(
                        "amazon" to product["amazon_url"]?.jsonPrimitive?.contentOrNull,
                        "open_food" to product["open_food_url"]?.jsonPrimitive?.contentOrNull,
                        "ebay" to product["ebay_url"]?.jsonPrimitive?.contentOrNull
                    ).filterValues { !it.isNullOrEmpty() }

                    // Build a ResultScreenItem object containing the fetched product info.
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

                    // Wrap the product details in a Success result.
                    Result.Success(result)
                } else {
                    // If "product" is missing or null, we treat it as product not found.
                    Result.Failure(Exception("Product not found"))
                }
            } else {
                // Non-200 HTTP responses are treated as errors.
                Result.Failure(Exception("HTTP error ${response.status}"))
            }

        } catch (e: Exception) {
            // Any networking or parsing errors are caught here and returned as a Failure.
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
            // Convert the domain model to a Room entity
            val entity = product.toEntity()

            // Insert the entity into the DB; Room returns the generated primary key
            val generatedId = scanResultDao.insert(entity)

            // Now retrieve the newly inserted entity from the database using the generated ID
            val insertedEntity = scanResultDao.getAll().find { it.id == generatedId }

            // If found, we construct a new ResultScreenItem with the actual ID from the DB
            if (insertedEntity != null) {
                val updatedProduct = product.copy(id = insertedEntity.id)
                Result.Success(updatedProduct)
            } else {
                Result.Failure(Exception("Error retrieving inserted product"))
            }
        } catch (e: Exception) {
            // Any database insertion or retrieval errors are caught and returned as a Failure.
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
            // Convert the domain model to a Room entity, then update it in the DB
            val entity = product.toEntity()
            scanResultDao.update(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            // Any database update errors are caught and returned as a Failure.
            Result.Failure(e)
        }
    }
}
