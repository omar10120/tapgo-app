package app.taplinks.vendor.di

import app.taplinks.vendor.platform.PlatformServices
import org.koin.dsl.module

actual val platformModule = module {
    single { PlatformServices(get()) } 
}