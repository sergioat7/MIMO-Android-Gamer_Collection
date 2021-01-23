package es.upsa.mimo.gamercollection.injection.components

import dagger.Component
import es.upsa.mimo.gamercollection.activities.GameDetailActivity
import es.upsa.mimo.gamercollection.activities.LandingActivity
import es.upsa.mimo.gamercollection.fragments.*
import es.upsa.mimo.gamercollection.fragments.popups.PopupFilterDialogFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupSyncAppDialogFragment
import es.upsa.mimo.gamercollection.injection.modules.AppDatabaseModule
import es.upsa.mimo.gamercollection.injection.modules.SharedPreferencesModule
import es.upsa.mimo.gamercollection.viewmodelfactories.LoginViewModelFactory

@Component(modules = [
    AppDatabaseModule::class,
    SharedPreferencesModule::class
])
interface AppComponent {

    fun inject(loginViewModelFactory: LoginViewModelFactory)

    fun inject(popupFilterDialogFragment: PopupFilterDialogFragment)
    fun inject(gameDetailFragment: GameDetailFragment)
    fun inject(gameDetailActivity: GameDetailActivity)
    fun inject(profileFragment: ProfileFragment)
    fun inject(gameSongsFragment: GameSongsFragment)
    fun inject(gamesFragment: GamesFragment)
    fun inject(registerFragment: RegisterFragment)
    fun inject(sagaDetailFragment: SagaDetailFragment)
    fun inject(sagasFragment: SagasFragment)
    fun inject(popupSyncAppDialogFragment: PopupSyncAppDialogFragment)
    fun inject(landingActivity: LandingActivity)
}