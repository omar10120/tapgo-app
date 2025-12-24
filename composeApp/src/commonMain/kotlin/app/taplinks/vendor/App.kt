package app.taplinks.vendor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.taplinks.vendor.di.appModule
import app.taplinks.vendor.di.platformModule
import app.taplinks.vendor.model.PaymentRequest
import app.taplinks.vendor.ui.screens.analytics.AnalyticsDashboardScreen
import app.taplinks.vendor.ui.screens.analytics.AnalyticsDashboardViewModel
import app.taplinks.vendor.ui.screens.createpayment.CreatePaymentRequestScreen
import app.taplinks.vendor.ui.screens.createpayment.CreatePaymentRequestViewModel
import app.taplinks.vendor.ui.screens.paymentdetails.PaymentRequestDetailsScreen
import app.taplinks.vendor.ui.screens.paymentdetails.PaymentRequestDetailsViewModel
import app.taplinks.vendor.ui.screens.paymentrequests.PaymentRequestsScreen
import app.taplinks.vendor.ui.screens.paymentrequests.PaymentRequestsViewModel
import app.taplinks.vendor.ui.screens.services.ServicesScreen
import app.taplinks.vendor.ui.screens.services.ServicesViewModel
import app.taplinks.vendor.ui.screens.signin.SignInScreen
import app.taplinks.vendor.ui.screens.signin.SignInViewModel
import app.taplinks.vendor.ui.screens.splash.SplashScreen
import app.taplinks.vendor.ui.screens.splash.SplashViewModel
import app.taplinks.vendor.ui.theme.VendorAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

enum class Screen { Splash, SignIn, PaymentRequestList, CreatePaymentRequest, PaymentRequestDetails, Analytics, Services }

@Composable
@Preview
fun App() {
    AppContent()
}

@Composable
fun AppContent() {
    VendorAppTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
        var selectedPaymentRequest by remember { mutableStateOf<PaymentRequest?>(null) }

        val splashViewModel: SplashViewModel = koinInject()
        val signInViewModel: SignInViewModel = koinInject()
        val paymentRequestsViewModel: PaymentRequestsViewModel = koinInject()
        val createPaymentRequestViewModel: CreatePaymentRequestViewModel = koinInject()
        val paymentRequestDetailsViewModel: PaymentRequestDetailsViewModel = koinInject()
        val analyticsDashboardViewModel: AnalyticsDashboardViewModel = koinInject()
        val servicesViewModel: ServicesViewModel = koinInject()

        when (currentScreen) {
            Screen.Splash -> {
                SplashScreen(
                    viewModel = splashViewModel,
                    onNavigateToSignIn = { currentScreen = Screen.SignIn },
                    onNavigateToPaymentRequests = { currentScreen = Screen.PaymentRequestList }
                )
            }

            Screen.SignIn -> {
                SignInScreen(
                    viewModel = signInViewModel,
                    onSignInSuccess = {
                        
                        signInViewModel.resetState()
                        currentScreen = Screen.PaymentRequestList
                    }
                )
            }

            Screen.PaymentRequestList -> {
                PaymentRequestsScreen(
                    viewModel = paymentRequestsViewModel,
                    onAddPaymentRequest = { currentScreen = Screen.CreatePaymentRequest },
                    onPaymentRequestSelected = { request ->
                        selectedPaymentRequest = request
                        currentScreen = Screen.PaymentRequestDetails
                    },
                    onAnalyticsClicked = { currentScreen = Screen.Analytics },
                    onServicesClicked = { currentScreen = Screen.Services },
                    onLogout = {
                        
                        currentScreen = Screen.SignIn
                    }
                )
            }

            Screen.CreatePaymentRequest -> {
                CreatePaymentRequestScreen(
                    viewModel = createPaymentRequestViewModel,
                    onNavigateBack = { currentScreen = Screen.PaymentRequestList },
                    onPaymentCreated = { paymentRequest ->
                        selectedPaymentRequest = paymentRequest
                        currentScreen = Screen.PaymentRequestDetails
                        
                        paymentRequestsViewModel.loadPaymentRequests()
                    }
                )
            }

            Screen.PaymentRequestDetails -> {
                PaymentRequestDetailsScreen(
                    viewModel = paymentRequestDetailsViewModel,
                    paymentRequest = selectedPaymentRequest,
                    onNavigateBack = {
                        currentScreen = Screen.PaymentRequestList
                        selectedPaymentRequest = null 
                    }
                )
            }

            Screen.Analytics -> {
                AnalyticsDashboardScreen(
                    viewModel = analyticsDashboardViewModel,
                    onNavigateBack = { currentScreen = Screen.PaymentRequestList }
                )
            }

            Screen.Services -> {
                ServicesScreen(
                    viewModel = servicesViewModel,
                    onNavigateBack = { currentScreen = Screen.PaymentRequestList }
                )
            }
        }
    }
}
