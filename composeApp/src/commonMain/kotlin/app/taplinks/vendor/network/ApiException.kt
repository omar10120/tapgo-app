package app.taplinks.vendor.network

import app.taplinks.vendor.model.api.BaseApiErrorResponse

sealed class ApiException(message: String) : Exception(message) {

    class NetworkError(message: String = "Network connection error") : ApiException(message)

    class ServerError(
        val statusCode: Int,
        val errorResponse: BaseApiErrorResponse?,
        message: String = "Server error"
    ) : ApiException(message)

    class UnauthorizedError(message: String = "Authentication failed") : ApiException(message)

    class NotFoundError(message: String = "Resource not found") : ApiException(message)

    class ClientError(
        val statusCode: Int,
        message: String = "Client error"
    ) : ApiException(message)

    class SerializationError(message: String = "Data parsing error") : ApiException(message)

    class TimeoutError(message: String = "Request timeout") : ApiException(message)

    class UnknownError(message: String = "Unknown error occurred") : ApiException(message)
}