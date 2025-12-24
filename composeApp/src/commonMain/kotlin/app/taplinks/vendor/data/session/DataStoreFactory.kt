package app.taplinks.vendor.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect object DataStoreFactory {
    fun createDataStore(): DataStore<Preferences>
}