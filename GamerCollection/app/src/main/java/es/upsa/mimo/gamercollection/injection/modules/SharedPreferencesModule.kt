package es.upsa.mimo.gamercollection.injection.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import es.upsa.mimo.gamercollection.utils.Constants

@Module
class SharedPreferencesModule(private val context: Context?) {

    @Provides
    fun provideSharedPreferences(): SharedPreferences? =
        context?.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
}