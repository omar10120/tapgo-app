package app.taplinks.vendor.data.auth

import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object AuthStateManager {
    private val mutex = Mutex()
    private val authenticatedHttpClients = mutableSetOf<HttpClient>()

    suspend fun registerAuthenticatedHttpClient(httpClient: HttpClient) {
        mutex.withLock {
            authenticatedHttpClients.add(httpClient)
        }
    }

    suspend fun invalidateAuthenticatedCaches() {
        mutex.withLock {
            authenticatedHttpClients.forEach { httpClient ->
                try {
                    val authProvider = httpClient.authProvider<BearerAuthProvider>()
                    authProvider?.clearToken()
                } catch (e: Exception) {
                    
                }
            }
        }
    }

}