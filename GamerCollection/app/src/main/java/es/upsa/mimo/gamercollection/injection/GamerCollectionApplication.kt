package es.upsa.mimo.gamercollection.injection

import android.app.Application
import es.upsa.mimo.gamercollection.injection.components.AppComponent
import es.upsa.mimo.gamercollection.injection.components.DaggerAppComponent
import es.upsa.mimo.gamercollection.injection.modules.AppDatabaseModule
import es.upsa.mimo.gamercollection.injection.modules.SharedPreferencesModule

class GamerCollectionApplication : Application() {

    //region Public properties
    lateinit var appComponent: AppComponent
    //endregion

    //region Lifecycle methods
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .sharedPreferencesModule(
                SharedPreferencesModule(applicationContext)
            )
            .appDatabaseModule(
                AppDatabaseModule(applicationContext)
            )
            .build()
    }
    //endregion
}