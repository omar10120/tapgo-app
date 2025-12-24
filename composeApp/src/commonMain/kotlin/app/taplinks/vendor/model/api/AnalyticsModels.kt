package app.taplinks.vendor.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ChartDataPointOutput(
    val timestamp: String,
    val value: Double
)

@Serializable
data class ActivityDataOutput(
    val description: String,
    val timeAgo: String,
    val amount: Double? = null
)

@Serializable
data class InsightDataOutput(
    val type: String, 
    val title: String,
    val description: String
)

@Serializable
data class PaymentStatusDistributionOutput(
    val status: String, 
    val count: Int
)

@Serializable
data class DashboardAnalyticsOutput(
    val totalRevenue: Double,
    val revenueChange: Double, 
    val totalPaymentLinks: Int,
    val paymentLinksChange: Int, 
    val successRate: Double,
    val successRateChange: Double, 
    val averageAmount: Double,
    val avgAmountChange: Double, 
    val revenueData: List<ChartDataPointOutput>,
    val statusDistribution: List<PaymentStatusDistributionOutput>,
    val recentActivity: List<ActivityDataOutput>,
    val insights: List<InsightDataOutput>
)