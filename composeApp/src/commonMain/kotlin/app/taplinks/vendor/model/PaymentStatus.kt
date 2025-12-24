package app.taplinks.vendor.model

enum class PaymentStatus(val apiValue: String) {
    PENDING("pending"),
    INITIATED("initiated"),
    PAID("paid"),
    EXPIRED("expired"),
    CANCELLED("cancelled");
    
    companion object {
        fun fromApiValue(value: String): PaymentStatus {
            return values().find { it.apiValue == value } ?: PENDING
        }
    }
}