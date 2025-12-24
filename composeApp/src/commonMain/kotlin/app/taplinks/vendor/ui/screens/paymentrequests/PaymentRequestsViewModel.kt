package app.taplinks.vendor.ui.screens.paymentrequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taplinks.vendor.model.PaymentRequest
import app.taplinks.vendor.model.PaymentStatus
import app.taplinks.vendor.network.ApiException
import app.taplinks.vendor.network.api.PaymentRequestApiService
import app.taplinks.vendor.data.auth.TokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PaymentRequestsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

class PaymentRequestsViewModel(
    private val paymentRequestApiService: PaymentRequestApiService,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentRequestsUiState())
    val uiState: StateFlow<PaymentRequestsUiState> = _uiState.asStateFlow()

    private val _allPaymentRequests = MutableStateFlow<List<PaymentRequest>>(emptyList())
    private val _selectedFilter = MutableStateFlow<PaymentStatus?>(null) 
    val selectedFilter: StateFlow<PaymentStatus?> = _selectedFilter.asStateFlow()

    val filteredPaymentRequests: StateFlow<List<PaymentRequest>> =
        combine(_allPaymentRequests, _selectedFilter) { requests, filter ->
            if (filter == null) {
                requests
            } else {
                requests.filter { it.status == filter }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectFilter(status: PaymentStatus?) {
        _selectedFilter.value = status
    }

    fun loadPaymentRequests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val isAuthenticated = tokenProvider.isAuthenticated().first()
            if (!isAuthenticated) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Authentication required. Please login again."
                )
                return@launch
            }

            paymentRequestApiService.getPaymentRequests().fold(
                onSuccess = { apiPaymentRequests ->
                    val paymentRequests = apiPaymentRequests.map { apiRequest ->
                        PaymentRequest.fromApiResponse(apiRequest)
                    }
                    _allPaymentRequests.value = paymentRequests
                    _uiState.value = _uiState.value.copy(isLoading = false)
                },
                onFailure = { exception ->
                    val errorMessage = when (exception) {
                        is ApiException.NetworkError -> "Network connection error. Please check your internet connection."
                        is ApiException.UnauthorizedError -> "Authentication failed. Please sign in again."
                        is ApiException.TimeoutError -> "Request timeout. Please try again."
                        is ApiException.ServerError -> "Server error. Please try again later."
                        else -> "Failed to load payment requests. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }

    fun refreshPaymentRequests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
            
            paymentRequestApiService.getPaymentRequests().fold(
                onSuccess = { apiPaymentRequests ->
                    val paymentRequests = apiPaymentRequests.map { apiRequest ->
                        PaymentRequest.fromApiResponse(apiRequest)
                    }
                    _allPaymentRequests.value = paymentRequests
                    _uiState.value = _uiState.value.copy(isRefreshing = false)
                },
                onFailure = { exception ->
                    val errorMessage = when (exception) {
                        is ApiException.NetworkError -> "Network connection error. Please check your internet connection."
                        is ApiException.UnauthorizedError -> "Authentication failed. Please sign in again."
                        is ApiException.TimeoutError -> "Request timeout. Please try again."
                        is ApiException.ServerError -> "Server error. Please try again later."
                        else -> "Failed to refresh payment requests. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                
                _allPaymentRequests.value = emptyList()
                _selectedFilter.value = null
                _uiState.value = PaymentRequestsUiState()

                tokenProvider.clearTokens()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Logout failed. Please try again."
                )
            }
        }
    }
}
