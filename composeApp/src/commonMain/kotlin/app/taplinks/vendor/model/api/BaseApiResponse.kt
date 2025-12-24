package app.taplinks.vendor.model.api

import kotlinx.serialization.Serializable

@Serializable
data class BaseApiResponse<T>(
    val meta: Map<String, String> = emptyMap(),
    val data: T
)

@Serializable
data class BaseApiErrorObject(
    val statusCode: Int,
    val message: String,
    val localizedMessage: String? = null,
    val errorName: String,
    val details: Map<String, String> = emptyMap(),
    val path: String,
    val requestId: String,
    val timestamp: String
)

@Serializable
data class BaseApiErrorResponse(
    val error: BaseApiErrorObject
)