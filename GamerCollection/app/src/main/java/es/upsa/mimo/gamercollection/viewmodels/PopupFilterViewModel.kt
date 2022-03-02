package es.upsa.mimo.gamercollection.viewmodels

import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.models.responses.FormatResponse
import es.upsa.mimo.gamercollection.models.responses.GenreResponse
import es.upsa.mimo.gamercollection.models.responses.PlatformResponse
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import javax.inject.Inject

class PopupFilterViewModel @Inject constructor(
    private val formatRepository: FormatRepository,
    private val genreRepository: GenreRepository,
    private val platformRepository: PlatformRepository
) : ViewModel() {

    //region Public properties
    val language: String
        get() = SharedPreferencesHelper.getLanguage()
    val filterDateFormat: String
        get() = SharedPreferencesHelper.getFilterDateFormat()
    val formats: List<FormatResponse>
        get() = formatRepository.getFormatsDatabase()
    val genres: List<GenreResponse>
        get() = genreRepository.getGenresDatabase()
    val platforms: List<PlatformResponse>
        get() = platformRepository.getPlatformsDatabase()
    //endregion
}