package app.taplinks.vendor.model.api

import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentRequestInput(
    val service: String,
    val amount: Double,
    val taxIncluded: Boolean,
    val expiry: String
)

@Serializable
data class UpdatePaymentRequestInput(
    val status: String? = null,
    val paymentInvoiceId: String? = null,
    val customerName: String? = null,
    val customerPhone: String? = null,
    val paidAt: String? = null
)

@Serializable
data class PaymentRequestUserOutput(
    val platformCharge: Double
)

@Serializable
data class PaymentRequestOutput(
    val id: Int,
    val service: String,
    val status: String, 
    val customerName: String? = null,
    val customerPhone: String? = null,
    val amount: Double,
    val tax: Double? = null,
    val taxIncluded: Boolean,
    val bookingNumber: String? = null,
    val expiry: String,
    val paidAt: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val user: PaymentRequestUserOutput
)