package es.upsa.mimo.gamercollection.injection.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.network.*
import javax.inject.Singleton

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
    fun providesRawgGameApiService(): RawgGameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT_RAWG
    )

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