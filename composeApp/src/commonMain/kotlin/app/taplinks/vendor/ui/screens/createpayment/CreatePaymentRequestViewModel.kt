package app.taplinks.vendor.ui.screens.createpayment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taplinks.vendor.model.ExpiryTime
import app.taplinks.vendor.model.PaymentRequest
import app.taplinks.vendor.network.ApiException
import app.taplinks.vendor.network.api.PaymentRequestApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

data class CreatePaymentRequestUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val createdPaymentRequest: PaymentRequest? = null
)

class CreatePaymentRequestViewModel(
    private val paymentRequestApiService: PaymentRequestApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CreatePaymentRequestUiState())
    val uiState: StateFlow<CreatePaymentRequestUiState> = _uiState.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _taxEnabled = MutableStateFlow(true)
    val taxEnabled: StateFlow<Boolean> = _taxEnabled.asStateFlow()

    private val _availableServices = MutableStateFlow(
        listOf("Haircut", "Manicure", "Massage", "Personal Training", "Hair Coloring")
    )
    val availableServices: StateFlow<List<String>> = _availableServices.asStateFlow()

    private val _selectedServices = MutableStateFlow<Set<String>>(emptySet())
    val selectedServices: StateFlow<Set<String>> = _selectedServices.asStateFlow()

    private val _availableExpiryTimes = MutableStateFlow(ExpiryTime.values().toList())
    val availableExpiryTimes: StateFlow<List<ExpiryTime>> = _availableExpiryTimes.asStateFlow()

    private val _selectedExpiryTime = MutableStateFlow(ExpiryTime.ONE_HOUR)
    val selectedExpiryTime: StateFlow<ExpiryTime> = _selectedExpiryTime.asStateFlow()
    
    private val _showAddServiceDialog = MutableStateFlow(false)
    val showAddServiceDialog: StateFlow<Boolean> = _showAddServiceDialog.asStateFlow()
    
    private val _newServiceName = MutableStateFlow("")
    val newServiceName: StateFlow<String> = _newServiceName.asStateFlow()

    fun onAmountChange(newAmount: String) {
        
        if (newAmount.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _amount.value = newAmount
        }
    }

    fun onTaxEnabledChange(enabled: Boolean) {
        _taxEnabled.value = enabled
    }

    fun toggleService(service: String) {
        _selectedServices.update {
            if (it.contains(service)) {
                it - service
            } else {
                it + service
            }
        }
    }

    fun selectExpiryTime(expiryTime: ExpiryTime) {
        _selectedExpiryTime.value = expiryTime
    }
    
    fun showAddServiceDialog() {
        _showAddServiceDialog.value = true
        _newServiceName.value = ""
    }
    
    fun hideAddServiceDialog() {
        _showAddServiceDialog.value = false
        _newServiceName.value = ""
    }
    
    fun onNewServiceNameChange(serviceName: String) {
        _newServiceName.value = serviceName
    }
    
    fun addNewService() {
        val serviceName = _newServiceName.value.trim()
        if (serviceName.isNotEmpty() && !_availableServices.value.contains(serviceName)) {
            _availableServices.value = _availableServices.value + serviceName
            _selectedServices.value = _selectedServices.value + serviceName
        }
        hideAddServiceDialog()
    }

    fun generatePaymentLink() {
        if (!validateInputs()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val service = _selectedServices.value.joinToString(", ")
            val enteredAmount = _amount.value.toDoubleOrNull() ?: 0.0
            val expiryInstant = calculateExpiryInstant(_selectedExpiryTime.value)
            
            paymentRequestApiService.createPaymentRequest(
                service = service,
                amount = enteredAmount,
                taxIncluded = _taxEnabled.value,
                expiry = expiryInstant.toString()
            ).fold(
                onSuccess = { apiPaymentRequest ->
                    val paymentRequest = PaymentRequest.fromApiResponse(apiPaymentRequest)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        createdPaymentRequest = paymentRequest
                    )
                },
                onFailure = { exception ->
                    val errorMessage = when (exception) {
                        is ApiException.NetworkError -> "Network connection error. Please check your internet connection."
                        is ApiException.UnauthorizedError -> "Authentication failed. Please sign in again."
                        is ApiException.TimeoutError -> "Request timeout. Please try again."
                        is ApiException.ServerError -> "Server error. Please try again later."
                        is ApiException.ClientError -> "Invalid request data. Please check your inputs."
                        else -> "Failed to create payment request. Please try again."
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }
    
    private fun validateInputs(): Boolean {
        val amountValue = _amount.value.toDoubleOrNull()
        
        when {
            amountValue == null || amountValue <= 0 -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid amount")
                return false
            }
            _selectedServices.value.isEmpty() -> {
                _uiState.value = _uiState.value.copy(errorMessage = "Please select at least one service")
                return false
            }
            else -> return true
        }
    }
    
    private fun calculateExpiryInstant(expiryTime: ExpiryTime): Instant {
        val now = Clock.System.now()
        return when (expiryTime) {
            ExpiryTime.ONE_HOUR -> now.plus(1.hours)
            ExpiryTime.THREE_HOURS -> now.plus(3.hours)
            ExpiryTime.TWELVE_HOURS -> now.plus(12.hours)
            ExpiryTime.TWENTY_FOUR_HOURS -> now.plus(24.hours)
        }
    }
    
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false, createdPaymentRequest = null)
    }
}