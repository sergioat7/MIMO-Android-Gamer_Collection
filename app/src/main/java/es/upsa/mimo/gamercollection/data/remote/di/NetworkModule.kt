package es.upsa.mimo.gamercollection.data.remote.di

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.data.remote.ApiManager
import es.upsa.mimo.gamercollection.data.remote.interfaces.RawgGameApiService
import javax.inject.Singleton

private const val BASE_ENDPOINT_RAWG = "https://api.rawg.io/api/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesRawgGameApiService(): RawgGameApiService = ApiManager.getService(BASE_ENDPOINT_RAWG)

    @Singleton
    @Provides
    fun providesFirebaseRemoteConfig(): FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            },
        )
        return remoteConfig
    }
}