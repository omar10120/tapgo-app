package app.taplinks.vendor.model.api

import kotlinx.serialization.Serializable

@Serializable
data class AddServiceInput(
    val serviceName: String
)

@Serializable
data class UpdateServiceInput(
    val oldServiceName: String,
    val newServiceName: String
)
