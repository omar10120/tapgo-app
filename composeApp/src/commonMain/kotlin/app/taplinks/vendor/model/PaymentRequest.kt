package app.taplinks.vendor.model

import app.taplinks.vendor.model.api.PaymentRequestOutput
import kotlinx.datetime.Instant

data class PaymentRequest(
    val id: Int,
    val serviceName: String,
    val customerName: String?,
    val customerPhone: String?,
    val amount: Double,
    val tax: Double?,
    val currency: String = "AED",
    val status: PaymentStatus,
    val taxIncluded: Boolean,
    val bookingNumber: String?,
    val expiry: Instant,
    val paidAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val platformCharge: Double
) {
    companion object {
        fun fromApiResponse(response: PaymentRequestOutput): PaymentRequest {
            return PaymentRequest(
                id = response.id,
                serviceName = response.service,
                customerName = response.customerName,
                customerPhone = response.customerPhone,
                amount = response.amount,
                tax = response.tax,
                status = PaymentStatus.fromApiValue(response.status),
                taxIncluded = response.taxIncluded,
                bookingNumber = response.bookingNumber,
                expiry = Instant.parse(response.expiry),
                paidAt = response.paidAt?.let { Instant.parse(it) },
                createdAt = Instant.parse(response.createdAt),
                updatedAt = Instant.parse(response.updatedAt),
                platformCharge = response.user.platformCharge
            )
        }
    }
}
