package app.taplinks.vendor.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

actual object DataStoreFactory {
    actual fun createDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            File(System.getProperty("user.home"), ".taplinks/preferences.preferences_pb")
        }
    }
}