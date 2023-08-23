package es.upsa.mimo.gamercollection

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GamerCollectionApplication : Application() {

    //region Static properties
    companion object {
        val context: Context
            get() = app.applicationContext
        private lateinit var app: GamerCollectionApplication
    }
    //endregion

    //region Lifecycle methods
    override fun onCreate() {
        super.onCreate()

        app = this
    }
    //endregion
}