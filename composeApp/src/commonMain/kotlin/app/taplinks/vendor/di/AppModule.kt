package app.taplinks.vendor.di

import app.taplinks.vendor.data.session.DataStoreFactory
import app.taplinks.vendor.data.auth.TokenProvider
import app.taplinks.vendor.data.auth.TokenProviderImpl
import app.taplinks.vendor.network.HttpClientFactory
import app.taplinks.vendor.network.api.AnalyticsApiService
import app.taplinks.vendor.network.api.AuthApiService
import app.taplinks.vendor.network.api.PaymentRequestApiService
import app.taplinks.vendor.network.api.VendorApiService
import app.taplinks.vendor.platform.PlatformServices
import app.taplinks.vendor.ui.screens.analytics.AnalyticsDashboardViewModel
import app.taplinks.vendor.ui.screens.createpayment.CreatePaymentRequestViewModel
import app.taplinks.vendor.ui.screens.paymentdetails.PaymentRequestDetailsViewModel
import app.taplinks.vendor.ui.screens.paymentrequests.PaymentRequestsViewModel
import app.taplinks.vendor.ui.screens.services.ServicesViewModel
import app.taplinks.vendor.ui.screens.signin.SignInViewModel
import app.taplinks.vendor.ui.screens.splash.SplashViewModel
import io.ktor.client.*
import org.koin.dsl.module

val appModule = module {

    single {
        DataStoreFactory.createDataStore()
    }
    single<TokenProvider> {
        TokenProviderImpl(get())
    }

    factory {
        HttpClientFactory(get())
    }
    factory<HttpClient> {
        get<HttpClientFactory>().create()
    }
    factory {
        
        val authHttpClient = get<HttpClientFactory>().create(isForAuth = true)
        AuthApiService(authHttpClient)
    }
    factory {
        PaymentRequestApiService(get())
    }
    factory {
        AnalyticsApiService(get())
    }
    factory {
        VendorApiService(get())
    }

    factory { SplashViewModel(get()) }
    factory { SignInViewModel(get(), get()) }
    factory { PaymentRequestsViewModel(get(), get()) }
    factory { CreatePaymentRequestViewModel(get()) }
    factory { PaymentRequestDetailsViewModel(get()) }
    factory { AnalyticsDashboardViewModel(get()) }
    factory { ServicesViewModel(get()) }
}
