package es.upsa.mimo.gamercollection.injection.components

import dagger.Component
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.fragments.*
import es.upsa.mimo.gamercollection.fragments.popups.PopupFilterDialogFragment
import es.upsa.mimo.gamercollection.injection.modules.AppDatabaseModule
import es.upsa.mimo.gamercollection.injection.modules.SharedPreferencesModule
import es.upsa.mimo.gamercollection.viewmodelfactories.*

@Component(modules = [
    AppDatabaseModule::class,
    SharedPreferencesModule::class
])
interface AppComponent {

    fun inject(landingViewModelFactory: LandingViewModelFactory)
    fun inject(loginViewModelFactory: LoginViewModelFactory)
    fun inject(popupSyncAppViewModelFactory: PopupSyncAppViewModelFactory)
    fun inject(profileViewModelFactory: ProfileViewModelFactory)
    fun inject(registerViewModelFactory: RegisterViewModelFactory)

    fun inject(popupFilterDialogFragment: PopupFilterDialogFragment)
    fun inject(gameDetailFragment: GameDetailFragment)
    fun inject(gameDetailActivity: GameDetailActivity)
    fun inject(gameSongsFragment: GameSongsFragment)
    fun inject(gamesFragment: GamesFragment)
    fun inject(sagaDetailFragment: SagaDetailFragment)
    fun inject(sagasFragment: SagasFragment)
}