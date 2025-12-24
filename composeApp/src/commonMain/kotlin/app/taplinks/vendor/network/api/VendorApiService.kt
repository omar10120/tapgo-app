package app.taplinks.vendor.network.api

import app.taplinks.vendor.model.api.AddServiceInput
import app.taplinks.vendor.model.api.BaseApiResponse
import app.taplinks.vendor.model.api.UpdateServiceInput
import app.taplinks.vendor.network.ApiResponseHandler
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class VendorApiService(
    private val httpClient: HttpClient
) {

    suspend fun getServices(): Result<List<String>> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<List<String>>> {
            httpClient.get("users/me/services")
        }.map { response ->
            response.data
        }
    }

    suspend fun addService(serviceName: String): Result<List<String>> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<List<String>>> {
            httpClient.post("users/me/services") {
                setBody(AddServiceInput(serviceName = serviceName))
            }
        }.map { response ->
            response.data
        }
    }

    suspend fun updateService(oldServiceName: String, newServiceName: String): Result<List<String>> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<List<String>>> {
            httpClient.patch("users/me/services") {
                setBody(UpdateServiceInput(
                    oldServiceName = oldServiceName,
                    newServiceName = newServiceName
                ))
            }
        }.map { response ->
            response.data
        }
    }

    suspend fun deleteService(serviceName: String): Result<List<String>> {
        return ApiResponseHandler.safeApiCall<BaseApiResponse<List<String>>> {
            httpClient.delete("users/me/services/${serviceName.encodeURLPath()}")
        }.map { response ->
            response.data
        }
    }
}
