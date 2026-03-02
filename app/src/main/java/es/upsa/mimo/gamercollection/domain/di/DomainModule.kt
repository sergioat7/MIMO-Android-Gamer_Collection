package es.upsa.mimo.gamercollection.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.data.GameRepositoryImpl
import es.upsa.mimo.gamercollection.data.SagaRepositoryImpl
import es.upsa.mimo.gamercollection.data.SongRepositoryImpl
import es.upsa.mimo.gamercollection.data.UserRepositoryImpl
import es.upsa.mimo.gamercollection.domain.GameRepository
import es.upsa.mimo.gamercollection.domain.SagaRepository
import es.upsa.mimo.gamercollection.domain.SongRepository
import es.upsa.mimo.gamercollection.domain.UserRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindGameRepository(repository: GameRepositoryImpl): GameRepository

    @Binds
    abstract fun bindSagaRepository(repository: SagaRepositoryImpl): SagaRepository

    @Binds
    abstract fun bindSongRepository(repository: SongRepositoryImpl): SongRepository

    @Binds
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository
}