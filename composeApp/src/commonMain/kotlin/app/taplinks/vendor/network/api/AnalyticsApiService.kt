package app.taplinks.vendor.network.api

import app.taplinks.vendor.model.api.BaseApiResponse
import app.taplinks.vendor.model.api.DashboardAnalyticsOutput
import app.taplinks.vendor.network.ApiResponseHandler
import io.ktor.client.*
import io.ktor.client.request.*

class AnalyticsApiService(
    private val httpClient: HttpClient
) {

    suspend fun getDashboardAnalytics(
        period: String = "LAST_30_DAYS"
    ): Result<DashboardAnalyticsOutput> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<DashboardAnalyticsOutput>> {
            httpClient.get("analytics/dashboard") {
                parameter("period", period)
            }
        }.map { response -> response.data }
    }
}