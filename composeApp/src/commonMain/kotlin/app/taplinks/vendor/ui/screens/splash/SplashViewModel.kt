package app.taplinks.vendor.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.taplinks.vendor.data.auth.TokenProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class SplashNavigationEvent {
    object NavigateToSignIn : SplashNavigationEvent()
    object NavigateToPaymentRequests : SplashNavigationEvent()
}

data class SplashUiState(
    val isLoading: Boolean = false,
    val navigationEvent: SplashNavigationEvent? = null
)

class SplashViewModel(
    private val tokenProvider: TokenProvider
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun checkLoginStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            delay(1500)
            
            try {
                
                val isLoggedIn = tokenProvider.isAuthenticated().first()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    navigationEvent = if (isLoggedIn) {
                        SplashNavigationEvent.NavigateToPaymentRequests
                    } else {
                        SplashNavigationEvent.NavigateToSignIn
                    }
                )
            } catch (e: Exception) {
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    navigationEvent = SplashNavigationEvent.NavigateToSignIn
                )
            }
        }
    }
    
    fun clearNavigationEvent() {
        _uiState.value = _uiState.value.copy(navigationEvent = null)
    }
}