package app.taplinks.vendor.network.api

import app.taplinks.vendor.model.api.AuthTokenOutput
import app.taplinks.vendor.model.api.BaseApiResponse
import app.taplinks.vendor.model.api.LoginInput
import app.taplinks.vendor.model.api.RefreshTokenInput
import app.taplinks.vendor.network.ApiResponseHandler
import io.ktor.client.*
import io.ktor.client.request.*

class AuthApiService(
    private val httpClient: HttpClient
) {

    suspend fun login(phoneNumber: String, password: String): Result<AuthTokenOutput> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<AuthTokenOutput>> {
            httpClient.post("auth/login") {
                setBody(LoginInput(phoneNumber = phoneNumber, password = password))
            }
        }.map { response ->
            response.data
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<AuthTokenOutput> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<AuthTokenOutput>> {
            httpClient.post("auth/refresh-token") {
                setBody(RefreshTokenInput(refreshToken = refreshToken))
            }
        }.map { response ->
            response.data
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}