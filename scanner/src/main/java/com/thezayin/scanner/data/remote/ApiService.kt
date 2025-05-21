package com.thezayin.scanner.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import timber.log.Timber

/**
 * ApiService responsible for making HTTP requests to the Open Food Facts API.
 */
class ApiService {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(Android) {
        install(HttpTimeout) {
            socketTimeoutMillis = 60000L
            requestTimeoutMillis = 60000L
            connectTimeoutMillis = 60000L
        }

        install(DefaultRequest) {
            headers.append(HttpHeaders.Accept, "application/json")
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.i(message)
                }
            }
        }

        install(ContentNegotiation) {
            json(json)
        }

        install(ResponseObserver) {
            onResponse { response ->
                Timber.i("HTTP Status: ${response.status.value}")
            }
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }

    /**
     * Fetches product details from the Open Food Facts API based on the provided barcode.
     * @param barcode The barcode string used to query the Open Food Facts API.
     * @return A JsonObject containing the product details if found, or null otherwise.
     */
    suspend fun fetchProductDetails(barcode: String): JsonObject? {
        try {
            val response = client.get {
                url("https://world.openfoodfacts.org/api/v0/product/$barcode.json")
            }

            if (response.status == HttpStatusCode.OK) {
                return response.body<JsonObject>()
            } else {
                Timber.e("HTTP error ${response.status}")
            }
        } catch (e: Exception) {
            Timber.e("Error fetching product details: ${e.localizedMessage}")
        }
        return null
    }
}
