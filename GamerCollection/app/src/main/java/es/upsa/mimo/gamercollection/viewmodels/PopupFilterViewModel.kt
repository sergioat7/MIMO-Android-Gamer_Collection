package es.upsa.mimo.gamercollection.viewmodels

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModel
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class PopupFilterViewModel @Inject constructor(
    sharedPreferencesHandler: SharedPreferencesHandler,
    formatRepository: FormatRepository,
    genreRepository: GenreRepository,
    platformRepository: PlatformRepository
): ViewModel() {

    //MARK: - Public properties

    val language: String = sharedPreferencesHandler.getLanguage()
    val formats: List<FormatResponse> = formatRepository.getFormats()
    val genres: List<GenreResponse> = genreRepository.getGenres()
    val platforms: List<PlatformResponse> = platformRepository.getPlatforms()

    //MARK: - Public methods

    fun getRoundedSelectorButton(id: String, text: String, context: Context): Button {

        val button = Button(context, null, R.style.RoundedSelectorButton, R.style.RoundedSelectorButton)
        button.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        button.tag = id
        button.text = text
        button.setOnClickListener { selectButton(it) }
        return button
    }

    //MARK: - Private methods

    private fun selectButton(button: View) {
        button.isSelected = !button.isSelected
    }
}