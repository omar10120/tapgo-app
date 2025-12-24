package app.taplinks.vendor.data.auth

import kotlinx.coroutines.flow.Flow
import app.taplinks.vendor.model.api.UserOutput

interface TokenProvider {
    
    suspend fun getToken(): Flow<TokenModel>

    suspend fun refreshToken(): Flow<TokenModel>

    suspend fun clearTokens()

    suspend fun isAuthenticated(): Flow<Boolean>

    suspend fun saveTokens(accessToken: String, refreshToken: String, userData: UserOutput)

    fun invalidateAuthCaches()
}

data class TokenModel(
    val accessToken: String = "",
    val refreshToken: String = "",
)