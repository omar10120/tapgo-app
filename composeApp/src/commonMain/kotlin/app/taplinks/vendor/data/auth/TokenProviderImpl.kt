package app.taplinks.vendor.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.taplinks.vendor.model.api.UserOutput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TokenProviderImpl(
    private val dataStore: DataStore<Preferences>
) : TokenProvider {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    override suspend fun getToken(): Flow<TokenModel> {
        return dataStore.data.map { preferences ->
            val accessToken = preferences[ACCESS_TOKEN_KEY].orEmpty()
            val refreshToken = preferences[REFRESH_TOKEN_KEY].orEmpty()

            TokenModel(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }.catch {
            emit(TokenModel()) 
        }
    }

    override suspend fun refreshToken(): Flow<TokenModel> {

        return getToken()
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_DATA_KEY)
        }

        invalidateAuthCaches()
    }

    override fun invalidateAuthCaches() {
        runBlocking {
            AuthStateManager.invalidateAuthenticatedCaches()
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String, userData: UserOutput) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_DATA_KEY] = json.encodeToString(userData)
        }
    }

    suspend fun getUserData(): Flow<UserOutput?> {
        return dataStore.data.map { preferences ->
            val userDataJson = preferences[USER_DATA_KEY]
            if (!userDataJson.isNullOrBlank()) {
                try {
                    json.decodeFromString<UserOutput>(userDataJson)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }.catch {
            emit(null)
        }
    }

    override suspend fun isAuthenticated(): Flow<Boolean> {
        return getToken().map { token ->
            token.accessToken.isNotBlank()
        }
    }
}