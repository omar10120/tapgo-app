package app.taplinks.vendor

import android.app.Application
import app.taplinks.vendor.data.session.DataStoreFactory
import app.taplinks.vendor.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class TapLinkApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        DataStoreFactory.initialize(this)

        initKoin(isDebug = true) {
            androidLogger()
            androidContext(this@TapLinkApp)
        }
    }
}