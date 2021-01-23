package es.upsa.mimo.gamercollection.injection

import android.app.Application
import es.upsa.mimo.gamercollection.injection.components.AppComponent
import es.upsa.mimo.gamercollection.injection.components.DaggerAppComponent
import es.upsa.mimo.gamercollection.injection.modules.AppDatabaseModule
import es.upsa.mimo.gamercollection.injection.modules.SharedPreferencesModule

class GamerCollectionApplication: Application() {

    //MARK: - Public properties

    lateinit var appComponent: AppComponent

    //MARK: - Lifecycle methods

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
}