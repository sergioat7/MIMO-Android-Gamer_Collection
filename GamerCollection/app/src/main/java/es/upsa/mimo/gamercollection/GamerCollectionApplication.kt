package es.upsa.mimo.gamercollection

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GamerCollectionApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    //region Lifecycle methods
    override fun onCreate() {
        super.onCreate()

        context = applicationContext
    }
    //endregion
}