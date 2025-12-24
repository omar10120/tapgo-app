package app.taplinks.vendor.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taplinks.vendor.model.PaymentStatus
import app.taplinks.vendor.model.api.DashboardAnalyticsOutput
import app.taplinks.vendor.network.api.AnalyticsApiService
import app.taplinks.vendor.network.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class AnalyticsDashboardViewModel(
    private val analyticsApiService: AnalyticsApiService
) : ViewModel() {

    private val _analyticsData = MutableStateFlow(AnalyticsData())
    val analyticsData: StateFlow<AnalyticsData> = _analyticsData.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(AnalyticsPeriod.LAST_30_DAYS)
    val selectedPeriod: StateFlow<AnalyticsPeriod> = _selectedPeriod.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun selectPeriod(period: AnalyticsPeriod) {
        _selectedPeriod.value = period
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val period = _selectedPeriod.value.name
                val result = analyticsApiService.getDashboardAnalytics(period)
                
                result.fold(
                    onSuccess = { apiResponse ->
                        _analyticsData.value = mapApiResponseToAnalyticsData(apiResponse)
                        _errorMessage.value = null
                    },
                    onFailure = { exception ->
                        val errorMessage = when (exception) {
                            is ApiException.NetworkError -> "Network connection error. Please check your internet connection and try again."
                            is ApiException.UnauthorizedError -> "Authentication failed. Please sign in again."
                            is ApiException.TimeoutError -> "Request timeout. Please try again."
                            is ApiException.ServerError -> "Server error. Please try again later."
                            is ApiException.ClientError -> "Unable to load analytics data. Please try again."
                            else -> "Failed to load analytics data. Please check your connection and try again."
                        }
                        _errorMessage.value = errorMessage
                    }
                )
                
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryLoadAnalytics() {
        loadAnalytics()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun mapApiResponseToAnalyticsData(apiResponse: DashboardAnalyticsOutput): AnalyticsData {
        
        val revenueData = apiResponse.revenueData.map { chartPoint ->
            ChartDataPoint(
                timestamp = Instant.parse(chartPoint.timestamp),
                value = chartPoint.value
            )
        }

        val statusDistribution = apiResponse.statusDistribution.associate { statusData ->
            PaymentStatus.fromApiValue(statusData.status) to statusData.count
        }

        val recentActivity = apiResponse.recentActivity.map { activity ->
            ActivityData(
                description = activity.description,
                timeAgo = activity.timeAgo,
                amount = activity.amount
            )
        }

        val insights = apiResponse.insights.map { insight ->
            InsightData(
                type = when (insight.type) {
                    "SUCCESS" -> InsightType.SUCCESS
                    "WARNING" -> InsightType.WARNING
                    "INFO" -> InsightType.INFO
                    else -> InsightType.INFO
                },
                title = insight.title,
                description = insight.description
            )
        }

        return AnalyticsData(
            totalRevenue = apiResponse.totalRevenue,
            revenueChange = apiResponse.revenueChange,
            totalPaymentLinks = apiResponse.totalPaymentLinks,
            paymentLinksChange = apiResponse.paymentLinksChange,
            successRate = apiResponse.successRate,
            successRateChange = apiResponse.successRateChange,
            averageAmount = apiResponse.averageAmount,
            avgAmountChange = apiResponse.avgAmountChange,
            revenueData = revenueData,
            statusDistribution = statusDistribution,
            recentActivity = recentActivity,
            insights = insights
        )
    }

}

enum class AnalyticsPeriod(val displayName: String) {
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    LAST_90_DAYS("Last 90 Days"),
    LAST_YEAR("Last Year")
}

data class AnalyticsData(
    val totalRevenue: Double = 0.0,
    val revenueChange: Double = 0.0, 
    val totalPaymentLinks: Int = 0,
    val paymentLinksChange: Int = 0, 
    val successRate: Double = 0.0,
    val successRateChange: Double = 0.0, 
    val averageAmount: Double = 0.0,
    val avgAmountChange: Double = 0.0, 
    val revenueData: List<ChartDataPoint> = emptyList(),
    val statusDistribution: Map<PaymentStatus, Int> = emptyMap(),
    val recentActivity: List<ActivityData> = emptyList(),
    val insights: List<InsightData> = emptyList()
)

data class ChartDataPoint(
    val timestamp: Instant,
    val value: Double
)

data class ActivityData(
    val description: String,
    val timeAgo: String,
    val amount: Double? = null
)

data class InsightData(
    val type: InsightType,
    val title: String,
    val description: String
)

enum class InsightType {
    SUCCESS,
    WARNING,
    INFO
}