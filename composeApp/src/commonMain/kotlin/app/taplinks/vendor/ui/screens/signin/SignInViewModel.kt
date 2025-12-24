package app.taplinks.vendor.ui.screens.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taplinks.vendor.data.auth.TokenProvider
import app.taplinks.vendor.network.ApiException
import app.taplinks.vendor.network.api.AuthApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SignInUiState(
    val phoneNumber: String = "",
    val password: String = "",
    val phoneNumberError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val errorMessage: String? = null
)

class SignInViewModel(
    private val authApiService: AuthApiService,
    private val tokenProvider: TokenProvider
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = phoneNumber,
            phoneNumberError = null
        )
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun signIn() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            authApiService.login(
                phoneNumber = _uiState.value.phoneNumber.trim(),
                password = _uiState.value.password
            ).fold(
                onSuccess = { authTokenOutput ->
                    viewModelScope.launch {
                        val accessTokenStart = authTokenOutput.accessToken.take(20) + "..."
                        println("[TAPLINK-AUTH] 🎉 SignInViewModel.signIn() - LOGIN SUCCESS")
                        println("[TAPLINK-AUTH] 🎉 SignInViewModel.signIn() - AccessToken: $accessTokenStart (length: ${authTokenOutput.accessToken.length})")
                        println("[TAPLINK-AUTH] 🎉 SignInViewModel.signIn() - RefreshToken length: ${authTokenOutput.refreshToken.length}")
                        println("[TAPLINK-AUTH] 🎉 SignInViewModel.signIn() - User: ${authTokenOutput.user.phoneNumber} (ID: ${authTokenOutput.user.id})")
                        println("[TAPLINK-AUTH] 🎉 SignInViewModel.signIn() - TokenProvider hashCode: ${tokenProvider.hashCode()}")

                        tokenProvider.saveTokens(
                            accessToken = authTokenOutput.accessToken,
                            refreshToken = authTokenOutput.refreshToken, 
                            userData = authTokenOutput.user
                        )

                        println("[TAPLINK-AUTH] ✅ SignInViewModel.signIn() - Tokens saved, updating UI state")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSignInSuccessful = true
                        )
                    }
                },
                onFailure = { exception ->
                    val errorMessage = when (exception) {
                        is ApiException.UnauthorizedError -> "Invalid phone number or password"
                        is ApiException.NetworkError -> "Network connection error. Please check your internet connection."
                        is ApiException.TimeoutError -> "Request timeout. Please try again."
                        is ApiException.ServerError -> "Server error. Please try again later."
                        else -> "Sign in failed. Please try again."
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
        val phoneNumber = _uiState.value.phoneNumber.trim()
        val password = _uiState.value.password

        var hasErrors = false
        var phoneNumberError: String? = null
        var passwordError: String? = null

        if (phoneNumber.isBlank()) {
            phoneNumberError = "Phone number is required"
            hasErrors = true
        } else if (!isValidPhoneNumber(phoneNumber)) {
            phoneNumberError = "Please enter a valid phone number"
            hasErrors = true
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            hasErrors = true
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            hasErrors = true
        }

        _uiState.value = _uiState.value.copy(
            phoneNumberError = phoneNumberError,
            passwordError = passwordError
        )

        return !hasErrors
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        
        val cleanNumber = phoneNumber.replace(Regex("[^\\d+]"), "")
        return cleanNumber.length >= 10 && (cleanNumber.startsWith("+") || cleanNumber.length <= 15)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetState() {
        _uiState.value = SignInUiState()
    }

}