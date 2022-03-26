package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.repositories.GameRepository
import es.upsa.mimo.gamercollection.repositories.SagaRepository
import es.upsa.mimo.gamercollection.viewmodels.PopupSyncAppViewModel
import javax.inject.Inject

class PopupSyncAppViewModelFactory(
    private val application: Application?
) : ViewModelProvider.Factory {

    //region Public properties
    @Inject
    lateinit var gameRepository: GameRepository

    @Inject
    lateinit var sagaRepository: SagaRepository

    @Inject
    lateinit var popupSyncAppViewModel: PopupSyncAppViewModel
    //endregion

    //region Lifecycle methods
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopupSyncAppViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            return popupSyncAppViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    //endregion
}