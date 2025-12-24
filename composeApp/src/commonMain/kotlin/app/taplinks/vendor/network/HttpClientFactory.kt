package app.taplinks.vendor.network

import app.taplinks.vendor.config.AppConfig
import app.taplinks.vendor.data.auth.AuthStateManager
import app.taplinks.vendor.data.auth.TokenProvider
import app.taplinks.vendor.model.api.AuthTokenOutput
import app.taplinks.vendor.model.api.BaseApiResponse
import app.taplinks.vendor.model.api.RefreshTokenInput
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val tokenProvider: TokenProvider
) {

    private fun isApiEndpoint(url: Url): Boolean {
        return url.host.contains(AppConfig.API_ENDPOINT)
    }

    fun create(isForAuth: Boolean = false): HttpClient {
        return HttpClient {
            expectSuccess = false
            addDefaultResponseValidation()

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                    explicitNulls = false
                })
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                filter { request ->
                    isApiEndpoint(request.url.build())
                }
            }

            defaultRequest {
                url(AppConfig.API_BASE_URL)
                contentType(ContentType.Application.Json)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val tokenModel = tokenProvider.getToken().firstOrNull()
                        tokenModel
                            ?.takeUnless { it.accessToken.isEmpty() }
                            ?.let { token ->
                                BearerTokens(
                                    accessToken = token.accessToken,
                                    refreshToken = token.refreshToken
                                )
                            }
                    }
                    refreshTokens {
                        val tokenModel = tokenProvider.getToken().firstOrNull()
                        tokenModel
                            ?.takeUnless { it.accessToken.isEmpty() }
                            ?.let { token ->
                                BearerTokens(
                                    accessToken = token.accessToken,
                                    refreshToken = token.refreshToken
                                )
                            }
                    }
                    sendWithoutRequest { request ->
                        isApiEndpoint(request.url.build())
                    }
                }
            }
        }.also { httpClient ->

            if (!isForAuth) {
                runBlocking {
                    AuthStateManager.registerAuthenticatedHttpClient(httpClient)
                }
            }
        }
    }
}