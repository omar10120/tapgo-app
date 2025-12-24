package app.taplinks.vendor.network

import app.taplinks.vendor.model.api.BaseApiErrorResponse
import io.ktor.client.call.*
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.serialization.*
import kotlinx.serialization.SerializationException

object ApiResponseHandler {

    suspend inline fun <reified T> safeApiCall(
        apiCall: () -> HttpResponse
    ): Result<T> {
        return try {
            val response = apiCall()
            val statusCode = response.status.value
            println("API Response - Status Code: $statusCode, URL: ${response.request.url}") 
            
            when {
                statusCode in 200..299 -> {
                    try {
                        val data = response.body<T>()
                        Result.success(data)
                    } catch (e: SerializationException) {
                        Result.failure(ApiException.SerializationError("Failed to parse response: ${e.message}"))
                    } catch (e: Exception) {
                        Result.failure(ApiException.SerializationError("Unexpected parsing error: ${e.message}"))
                    }
                }
                statusCode == 401 -> {
                    Result.failure(ApiException.UnauthorizedError("Authentication failed"))
                }
                statusCode == 404 -> {
                    Result.failure(ApiException.NotFoundError("Resource not found"))
                }
                statusCode in 400..499 -> {
                    val errorResponse = try {
                        response.body<BaseApiErrorResponse>()
                    } catch (e: Exception) {
                        null
                    }
                    Result.failure(
                        ApiException.ClientError(
                            statusCode = statusCode,
                            message = errorResponse?.error?.message ?: "Client error"
                        )
                    )
                }
                statusCode >= 500 -> {
                    val errorResponse = try {
                        response.body<BaseApiErrorResponse>()
                    } catch (e: Exception) {
                        null
                    }
                    Result.failure(
                        ApiException.ServerError(
                            statusCode = statusCode,
                            errorResponse = errorResponse,
                            message = errorResponse?.error?.message ?: "Server error"
                        )
                    )
                }
                else -> {
                    Result.failure(ApiException.UnknownError("Unexpected status code: $statusCode"))
                }
            }
        } catch (e: Exception) {
            println("API Exception caught: ${e::class.simpleName} - ${e.message}") 
            Result.failure(mapException(e))
        }
    }

    fun mapException(exception: Exception): ApiException {
        return when (exception) {
            is ApiException -> exception
            is HttpRequestTimeoutException -> ApiException.TimeoutError("Request timeout")
            is ConnectTimeoutException -> ApiException.TimeoutError("Connection timeout")
            is SocketTimeoutException -> ApiException.TimeoutError("Socket timeout")
            is SerializationException -> ApiException.SerializationError("Data parsing error: ${exception.message}")
            is ResponseException -> {
                when (exception.response.status.value) {
                    401 -> ApiException.UnauthorizedError("Authentication failed")
                    404 -> ApiException.NotFoundError("Resource not found")
                    in 400..499 -> ApiException.ClientError(
                        statusCode = exception.response.status.value,
                        message = "Client error: ${exception.message}"
                    )
                    in 500..599 -> ApiException.ServerError(
                        statusCode = exception.response.status.value,
                        message = "Server error: ${exception.message}",
                        errorResponse = BaseApiErrorResponse(error("Server error"))
                    )
                    else -> ApiException.UnknownError("HTTP error: ${exception.message}")
                }
            }
            else -> {
                
                val message = exception.message?.lowercase() ?: ""
                when {
                    message.contains("network") || message.contains("connection") -> 
                        ApiException.NetworkError("Network connection error - Check emulator network settings")
                    message.contains("timeout") -> 
                        ApiException.TimeoutError("Request timeout")
                    message.contains("unreachable") || message.contains("resolve") ->
                        ApiException.NetworkError("Cannot reach server - Check emulator DNS/network configuration")
                    else -> 
                        ApiException.UnknownError("Unexpected error (Status: -1 may indicate emulator network issue): ${exception.message}")
                }
            }
        }
    }
}