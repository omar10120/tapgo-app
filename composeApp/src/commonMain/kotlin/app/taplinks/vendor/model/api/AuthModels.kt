package app.taplinks.vendor.model.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginInput(
    val phoneNumber: String,
    val password: String
)

@Serializable
data class RefreshTokenInput(
    val refreshToken: String
)

@Serializable
data class UserOutput(
    val id: Int,
    val phoneNumber: String,
    val roles: List<String>,

)

@Serializable
data class AuthTokenOutput(
    val accessToken: String,
    val refreshToken: String,
    val user: UserOutput
)