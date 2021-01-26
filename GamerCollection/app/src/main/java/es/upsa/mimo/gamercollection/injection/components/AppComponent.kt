package es.upsa.mimo.gamercollection.injection.components

import dagger.Component
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.fragments.*
import es.upsa.mimo.gamercollection.injection.modules.AppDatabaseModule
import es.upsa.mimo.gamercollection.injection.modules.SharedPreferencesModule
import es.upsa.mimo.gamercollection.viewmodelfactories.*

@Component(modules = [
    AppDatabaseModule::class,
    SharedPreferencesModule::class
])
interface AppComponent {

    fun inject(gameDataViewModelFactory: GameDataViewModelFactory)
    fun inject(gameDetailViewModelFactory: GameDetailViewModelFactory)
    fun inject(gameSongsViewModelFactory: GameSongsViewModelFactory)
    fun inject(gamesViewModelFactory: GamesViewModelFactory)
    fun inject(landingViewModelFactory: LandingViewModelFactory)
    fun inject(loginViewModelFactory: LoginViewModelFactory)
    fun inject(popupFilterViewModelFactory: PopupFilterViewModelFactory)
    fun inject(popupSyncAppViewModelFactory: PopupSyncAppViewModelFactory)
    fun inject(profileViewModelFactory: ProfileViewModelFactory)
    fun inject(registerViewModelFactory: RegisterViewModelFactory)
    fun inject(sagaDetailViewModelFactory: SagaDetailViewModelFactory)
    fun inject(sagasViewModelFactory: SagasViewModelFactory)
}