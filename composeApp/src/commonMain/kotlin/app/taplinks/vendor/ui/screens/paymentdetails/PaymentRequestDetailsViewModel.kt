package app.taplinks.vendor.ui.screens.paymentdetails

import androidx.lifecycle.ViewModel
import app.taplinks.vendor.config.AppConfig
import app.taplinks.vendor.model.PaymentRequest
import app.taplinks.vendor.platform.PlatformServices
import app.taplinks.vendor.utils.MoneyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PaymentRequestDetailsViewModel(
    private val platformServices: PlatformServices
) : ViewModel() {

    private val _paymentRequest = MutableStateFlow<PaymentRequest?>(null)
    val paymentRequest: StateFlow<PaymentRequest?> = _paymentRequest.asStateFlow()

    private val _serviceDetailsList = MutableStateFlow<List<String>>(emptyList())
    val serviceDetailsList: StateFlow<List<String>> = _serviceDetailsList.asStateFlow()

    private val _showInfoBottomSheet = MutableStateFlow(false)
    val showInfoBottomSheet: StateFlow<Boolean> = _showInfoBottomSheet.asStateFlow()

    val platformCharges: Double 
        get() = _paymentRequest.value?.platformCharge ?: 0.0

    val taxStatus: String 
        get() = when (_paymentRequest.value?.taxIncluded) {
            true -> "Included"
            false -> "Excluded"
            null -> "N/A"
        }

    fun setPaymentRequest(request: PaymentRequest) {
        _paymentRequest.value = request
        
        if (request.serviceName.contains(",")) {
            _serviceDetailsList.value = request.serviceName.split(",").map { it.trim() }
        } else {
            _serviceDetailsList.value = listOf(request.serviceName)
        }
    }

    fun getVatAmount(): String {
        val tax = _paymentRequest.value?.tax
        return if (tax != null) {
            MoneyFormatter.format(tax)
        } else {
            "Included"
        }
    }
    
    fun getSubTotal(): String {
        val paymentRequest = _paymentRequest.value
        if (paymentRequest?.tax != null) {
            val baseAmount = paymentRequest.amount - paymentRequest.tax
            return MoneyFormatter.format(baseAmount)
        }
        return MoneyFormatter.format(paymentRequest?.amount ?: 0.0)
    }

    fun getCalculatedTotal(): String {
        val totalAmount = _paymentRequest.value?.amount ?: 0.0
        val calculatedTotal = totalAmount + platformCharges
        return MoneyFormatter.format(calculatedTotal)
    }

    fun onCopyLink() {
        _paymentRequest.value?.let { request ->
            val paymentLink = generatePaymentLink(request.bookingNumber)
            platformServices.copyToClipboard(paymentLink)
        }
    }

    fun onShareLink() {
        _paymentRequest.value?.let { request ->
            val paymentLink = generatePaymentLink(request.bookingNumber)
            val formattedAmount = MoneyFormatter.format(request.amount)
            val shareText = "Payment request for ${request.serviceName} - Amount: AED ${formattedAmount}\n\nPay now: $paymentLink"
            platformServices.shareText(shareText, "Payment Request")
        }
    }

    fun onWhatsAppMessage() {
        _paymentRequest.value?.let { request ->
            request.customerPhone?.let { phoneNumber ->
                val paymentLink = generatePaymentLink(request.bookingNumber)
                val formattedAmount = MoneyFormatter.format(request.amount)
                val message = "Hi! You have a payment request for ${request.serviceName} - Amount: AED ${formattedAmount}. Please pay using this link: $paymentLink"
                platformServices.openWhatsApp(phoneNumber, message)
            }
        }
    }
    
    fun showInfoBottomSheet() {
        _showInfoBottomSheet.value = true
    }
    
    fun hideInfoBottomSheet() {
        _showInfoBottomSheet.value = false
    }
    
    private fun generatePaymentLink(bookingNumber: String? = ""): String {
        return "${AppConfig.Endpoints.PAYMENT_PAGE}/$bookingNumber"
    }
}
