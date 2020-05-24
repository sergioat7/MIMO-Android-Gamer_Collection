package es.upsa.mimo.gamercollection.fragments.popups

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.persistence.repositories.FormatRepository
import es.upsa.mimo.gamercollection.persistence.repositories.GenreRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.Constants
import kotlinx.android.synthetic.main.fragment_popup_filter_dialog.*

class PopupFilterDialogFragment : DialogFragment() {

    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popup_filter_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formatRepository = FormatRepository(requireContext())
        genreRepository = GenreRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())

        initializeUI()
    }

    //MARK: - Private functions

    private fun initializeUI() {

        fillPlatforms()
        fillGenres()
        fillFormats()

        edit_text_release_date_min.showDatePicker(requireContext())
        edit_text_release_date_min.setRawInputType(InputType.TYPE_NULL)
        edit_text_release_date_max.showDatePicker(requireContext())
        edit_text_release_date_max.setRawInputType(InputType.TYPE_NULL)

        edit_text_purchase_date_min.showDatePicker(requireContext())
        edit_text_purchase_date_min.setRawInputType(InputType.TYPE_NULL)
        edit_text_purchase_date_max.showDatePicker(requireContext())
        edit_text_purchase_date_max.setRawInputType(InputType.TYPE_NULL)

        button_cancel.setOnClickListener { cancel() }
        button_reset.setOnClickListener { reset() }
        button_save.setOnClickListener { save() }
    }

    private fun fillPlatforms() {

        linear_layout_platforms.removeAllViews()
        val platforms = platformRepository.getPlatforms()
        for (platform in platforms) {

            val button = getRoundedSelectorButton(platform.id, platform.name)

            val view = View(requireContext())
            view.layoutParams = ViewGroup.LayoutParams(
                20,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            linear_layout_platforms.addView(button)
            linear_layout_platforms.addView(view)
        }
        linear_layout_platforms.removeViewAt(linear_layout_platforms.childCount - 1)
    }

    private fun fillGenres() {

        linear_layout_genres.removeAllViews()
        val genres = genreRepository.getGenres()
        for (genre in genres) {

            val button = getRoundedSelectorButton(genre.id, genre.name)

            val view = View(requireContext())
            view.layoutParams = ViewGroup.LayoutParams(
                20,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            linear_layout_genres.addView(button)
            linear_layout_genres.addView(view)
        }
        linear_layout_genres.removeViewAt(linear_layout_genres.childCount - 1)
    }

    private fun fillFormats() {

        linear_layout_formats.removeAllViews()
        val formats = formatRepository.getFormats()
        for (format in formats) {

            val button = getRoundedSelectorButton(format.id, format.name)

            val view = View(requireContext())
            view.layoutParams = ViewGroup.LayoutParams(
                20,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            linear_layout_formats.addView(button)
            linear_layout_formats.addView(view)
        }
        linear_layout_formats.removeViewAt(linear_layout_formats.childCount - 1)
    }

    private fun cancel() {
        dismiss()
    }

    private fun reset() {

        for (child in linear_layout_platforms.children) {
            child.isSelected = false
        }

        for (child in linear_layout_genres.children) {
            child.isSelected = false
        }

        for (child in linear_layout_formats.children) {
            child.isSelected = false
        }

        rating_bar_min.rating = 0F
        rating_bar_max.rating = 5F

        edit_text_release_date_min.setText("")
        edit_text_release_date_max.setText("")

        edit_text_purchase_date_min.setText("")
        edit_text_purchase_date_max.setText("")

        edit_text_price_min.setText("")
        edit_text_price_max.setText("")

        radio_button_goty_no.isChecked = true

        radio_button_loaned_no.isChecked = true

        radio_button_saga_no.isChecked = true

        radio_button_songs_no.isChecked = true
    }

    private fun save() {

        var platforms: List<String> = arrayListOf()
        for (child in linear_layout_platforms.children) {
            if (child.isSelected) platforms += "${child.tag}"
        }

        var genres: List<String> = arrayListOf()
        for (child in linear_layout_genres.children) {
            if (child.isSelected) genres += "${child.tag}"
        }

        var formats: List<String> = arrayListOf()
        for (child in linear_layout_formats.children) {
            if (child.isSelected) formats += "${child.tag}"
        }

        val minScore = rating_bar_min.rating * 2
        val maxScore = rating_bar_max.rating * 2

        val minReleaseDate = Constants.stringToDate(edit_text_release_date_min.text.toString())
        val maxReleaseDate = Constants.stringToDate(edit_text_release_date_max.text.toString())

        val minPurchaseDate = Constants.stringToDate(edit_text_purchase_date_min.text.toString())
        val maxPurchaseDate = Constants.stringToDate(edit_text_purchase_date_max.text.toString())

        val minPrice = edit_text_price_min.text.toString().toDouble()
        val maxPrice = edit_text_price_max.text.toString().toDouble()

        val isGoty = radio_button_goty_yes.isChecked

        val isLoaned = radio_button_loaned_yes.isChecked

        val hasSaga = radio_button_saga_yes.isChecked
        val hasSongs = radio_button_songs_yes.isChecked

        dismiss()
    }

    private fun getRoundedSelectorButton(id: String, text: String): Button {

        val button = Button(requireContext(), null, R.style.RoundedSelectorButton, R.style.RoundedSelectorButton)
        button.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        button.tag = id
        button.text = text
        button.setOnClickListener { selectButton(it) }
        return button
    }

    private fun selectButton(button: View) {
        button.isSelected = !button.isSelected
    }
}
