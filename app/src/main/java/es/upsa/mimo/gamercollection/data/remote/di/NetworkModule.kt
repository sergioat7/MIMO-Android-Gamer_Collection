package es.upsa.mimo.gamercollection.data.remote.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.data.remote.ApiManager
import es.upsa.mimo.gamercollection.data.remote.interfaces.GameApiService
import es.upsa.mimo.gamercollection.data.remote.interfaces.RawgGameApiService
import es.upsa.mimo.gamercollection.data.remote.interfaces.SagaApiService
import es.upsa.mimo.gamercollection.data.remote.interfaces.SongApiService
import es.upsa.mimo.gamercollection.data.remote.interfaces.UserApiService
import javax.inject.Singleton

private const val BASE_ENDPOINT_RAWG = "https://api.rawg.io/api/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesGameApiService(): GameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Singleton
    @Provides
    fun providesRawgGameApiService(): RawgGameApiService = ApiManager.getService(BASE_ENDPOINT_RAWG)

    @Singleton
    @Provides
    fun providesSagaApiService(): SagaApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Singleton
    @Provides
    fun providesSongApiService(): SongApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Singleton
    @Provides
    fun providesUserApiService(): UserApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )
}