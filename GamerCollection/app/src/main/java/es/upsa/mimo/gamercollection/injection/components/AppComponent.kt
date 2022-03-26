package es.upsa.mimo.gamercollection.injection.components

import dagger.Component
import es.upsa.mimo.gamercollection.injection.modules.AppDatabaseModule
import es.upsa.mimo.gamercollection.injection.modules.DispatcherModule
import es.upsa.mimo.gamercollection.injection.modules.NetworkModule
import es.upsa.mimo.gamercollection.viewmodelfactories.*

@Component(
    modules = [
        AppDatabaseModule::class,
        DispatcherModule::class,
        NetworkModule::class
    ]
)
interface AppComponent {

    fun inject(gameDetailViewModelFactory: GameDetailViewModelFactory)
    fun inject(gameSongsViewModelFactory: GameSongsViewModelFactory)
    fun inject(gamesViewModelFactory: GamesViewModelFactory)
    fun inject(gameSearchViewModelFactory: GameSearchViewModelFactory)
    fun inject(landingViewModelFactory: LandingViewModelFactory)
    fun inject(loginViewModelFactory: LoginViewModelFactory)
    fun inject(popupSyncAppViewModelFactory: PopupSyncAppViewModelFactory)
    fun inject(registerViewModelFactory: RegisterViewModelFactory)
    fun inject(sagaDetailViewModelFactory: SagaDetailViewModelFactory)
    fun inject(sagasViewModelFactory: SagasViewModelFactory)
    fun inject(settingsViewModelFactory: SettingsViewModelFactory)
}