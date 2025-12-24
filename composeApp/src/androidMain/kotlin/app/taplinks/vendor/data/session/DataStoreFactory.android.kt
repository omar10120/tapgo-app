package app.taplinks.vendor.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "taplinks_session")

actual object DataStoreFactory {
    @Volatile
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context.applicationContext 
    }
    
    actual fun createDataStore(): DataStore<Preferences> {
        val ctx = context ?: throw IllegalStateException(
            "DataStoreFactory must be initialized before creating DataStore. " +
            "Call DataStoreFactory.initialize(context) in your Application.onCreate()"
        )
        return ctx.dataStore
    }
}