package es.upsa.mimo.gamercollection.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.viewmodels.PopupFilterViewModel
import javax.inject.Inject

class PopupFilterViewModelFactory(
    private val application: Application?
) : ViewModelProvider.Factory {

    //region Public properties
    @Inject
    lateinit var formatRepository: FormatRepository

    @Inject
    lateinit var genreRepository: GenreRepository

    @Inject
    lateinit var platformRepository: PlatformRepository

    @Inject
    lateinit var popupFilterViewModel: PopupFilterViewModel
    //endregion

    //region Lifecycle methods
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopupFilterViewModel::class.java)) {

            (application as GamerCollectionApplication).appComponent.inject(this)
            return popupFilterViewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    //endregion
}