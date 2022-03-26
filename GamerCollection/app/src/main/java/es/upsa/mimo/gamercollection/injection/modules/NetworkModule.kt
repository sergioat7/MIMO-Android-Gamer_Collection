package es.upsa.mimo.gamercollection.injection.modules

import dagger.Module
import dagger.Provides
import es.upsa.mimo.gamercollection.network.*

@Module
class NetworkModule {

    @Provides
    fun providesGameApiService(): GameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesRawgGameApiService(): RawgGameApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT_RAWG
    )

    @Provides
    fun providesSagaApiService(): SagaApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesSongApiService(): SongApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )

    @Provides
    fun providesUserApiService(): UserApiService = ApiManager.getService(
        ApiManager.BASE_ENDPOINT
    )
}