package app.taplinks.vendor

import androidx.compose.ui.window.ComposeUIViewController
import app.taplinks.vendor.di.appModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule)
    }
    App()
}
