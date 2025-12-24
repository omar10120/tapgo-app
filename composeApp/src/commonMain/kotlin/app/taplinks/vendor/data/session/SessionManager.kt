package app.taplinks.vendor.data.session

import app.taplinks.vendor.model.api.UserOutput
import kotlinx.coroutines.flow.Flow

interface SessionManager {

    val isAuthenticated: Flow<Boolean>

    val accessToken: Flow<String?>

    val userData: Flow<UserOutput?>

    suspend fun saveSession(accessToken: String, userData: UserOutput)

    suspend fun getAccessToken(): String?

    suspend fun getUserData(): UserOutput?

    suspend fun clearSession()

    suspend fun isUserAuthenticated(): Boolean
}