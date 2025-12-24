package app.taplinks.vendor.network.api

import app.taplinks.vendor.model.api.BaseApiResponse
import app.taplinks.vendor.model.api.CreatePaymentRequestInput
import app.taplinks.vendor.model.api.PaymentRequestOutput
import app.taplinks.vendor.network.ApiResponseHandler
import io.ktor.client.*
import io.ktor.client.request.*

class PaymentRequestApiService(
    private val httpClient: HttpClient
) {

    suspend fun getPaymentRequests(
        offset: Int = 0,
        limit: Int = 20
    ): Result<List<PaymentRequestOutput>> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<List<PaymentRequestOutput>>> {
            httpClient.get("payment-requests") {
                parameter("offset", offset)
                parameter("limit", limit)
            }
        }.map { response ->
            response.data
        }
    }

    suspend fun createPaymentRequest(
        service: String,
        amount: Double,
        taxIncluded: Boolean,
        expiry: String
    ): Result<PaymentRequestOutput> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<PaymentRequestOutput>> {
            httpClient.post("payment-requests") {
                setBody(
                    CreatePaymentRequestInput(
                        service = service,
                        amount = amount,
                        taxIncluded = taxIncluded,
                        expiry = expiry
                    )
                )
            }
        }.map { response ->
            response.data
        }
    }

    suspend fun getPaymentRequest(id: Int): Result<PaymentRequestOutput> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<PaymentRequestOutput>> {
            httpClient.get("payment-requests/$id")
        }.map { response ->
            response.data
        }
    }
}