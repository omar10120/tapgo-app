package app.taplinks.vendor.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.taplinks.vendor.model.api.UserOutput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionManagerImpl(
    private val dataStore: DataStore<Preferences>
) : SessionManager {
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val USER_DATA_KEY = stringPreferencesKey("user_data")
    }
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    override val isAuthenticated: Flow<Boolean> = dataStore.data.map { preferences ->
        !preferences[ACCESS_TOKEN_KEY].isNullOrBlank()
    }
    
    override val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }
    
    override val userData: Flow<UserOutput?> = dataStore.data.map { preferences ->
        val userDataJson = preferences[USER_DATA_KEY]
        if (userDataJson != null) {
            try {
                json.decodeFromString<UserOutput>(userDataJson)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    override suspend fun saveSession(accessToken: String, userData: UserOutput) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[USER_DATA_KEY] = json.encodeToString(userData)
        }
    }
    
    override suspend fun getAccessToken(): String? {
        return accessToken.first()
    }
    
    override suspend fun getUserData(): UserOutput? {
        return userData.first()
    }
    
    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(USER_DATA_KEY)
        }
    }
    
    override suspend fun isUserAuthenticated(): Boolean {
        return isAuthenticated.first()
    }
}