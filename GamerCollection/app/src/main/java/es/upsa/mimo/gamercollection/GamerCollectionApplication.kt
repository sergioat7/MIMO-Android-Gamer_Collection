package es.upsa.mimo.gamercollection

import android.app.Application
import android.content.Context
import es.upsa.mimo.gamercollection.injection.components.AppComponent
import es.upsa.mimo.gamercollection.injection.components.DaggerAppComponent
import es.upsa.mimo.gamercollection.injection.modules.AppDatabaseModule
import es.upsa.mimo.gamercollection.injection.modules.DispatcherModule
import es.upsa.mimo.gamercollection.injection.modules.NetworkModule

class GamerCollectionApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    //region Public properties
    lateinit var appComponent: AppComponent
    //endregion

    //region Lifecycle methods
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appDatabaseModule(
                AppDatabaseModule(applicationContext)
            )
            .dispatcherModule(DispatcherModule())
            .networkModule(NetworkModule())
            .build()

        context = applicationContext
    }
    //endregion
}