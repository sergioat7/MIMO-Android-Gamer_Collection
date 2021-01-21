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
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.repositories.FormatRepository
import es.upsa.mimo.gamercollection.repositories.GenreRepository
import es.upsa.mimo.gamercollection.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_popup_filter_dialog.*
import javax.inject.Inject

class PopupFilterDialogFragment(
    private var currentFilters: FilterModel?,
    private val onFiltersSelected: OnFiltersSelected
) : DialogFragment() {

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    @Inject
    lateinit var formatRepository: FormatRepository
    @Inject
    lateinit var genreRepository: GenreRepository
    @Inject
    lateinit var platformRepository: PlatformRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popup_filter_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = activity?.application
        (application as GamerCollectionApplication).appComponent.inject(this)

        initializeUI()
        configFilters(currentFilters)
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

    private fun configFilters(filters: FilterModel?) {

        filters?.let {

            val platforms = filters.platforms
            if (platforms.isNotEmpty()) {
                for (child in linear_layout_platforms.children) {
                    child.isSelected = platforms.firstOrNull { it == child.tag } != null
                }
            }

            val genres = filters.genres
            if (genres.isNotEmpty()) {
                for (child in linear_layout_genres.children) {
                    child.isSelected = genres.firstOrNull { it == child.tag } != null
                }
            }

            val formats = filters.formats
            if (formats.isNotEmpty()) {
                for (child in linear_layout_formats.children) {
                    child.isSelected = formats.firstOrNull { it == child.tag } != null
                }
            }

            rating_bar_min.rating = (filters.minScore / 2).toFloat()
            rating_bar_max.rating = (filters.maxScore / 2).toFloat()

            edit_text_release_date_min.setText(Constants.dateToString(filters.minReleaseDate, sharedPrefHandler))
            edit_text_release_date_max.setText(Constants.dateToString(filters.maxReleaseDate, sharedPrefHandler))

            edit_text_purchase_date_min.setText(Constants.dateToString(filters.minPurchaseDate, sharedPrefHandler))
            edit_text_purchase_date_max.setText(Constants.dateToString(filters.maxPurchaseDate, sharedPrefHandler))

            if (filters.minPrice > 0) edit_text_price_min.setText(filters.minPrice.toString())
            if (filters.maxPrice > 0) edit_text_price_max.setText(filters.maxPrice.toString())

            radio_button_goty_yes.isChecked = filters.isGoty

            radio_button_loaned_yes.isChecked = filters.isLoaned

            radio_button_saga_yes.isChecked = filters.hasSaga

            radio_button_songs_yes.isChecked = filters.hasSongs
        }
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

        currentFilters = null
    }

    private fun save() {

        val platforms: ArrayList<String> = arrayListOf()
        for (child in linear_layout_platforms.children) {
            if (child.isSelected) platforms.add("${child.tag}")
        }

        val genres: ArrayList<String> = arrayListOf()
        for (child in linear_layout_genres.children) {
            if (child.isSelected) genres.add("${child.tag}")
        }

        val formats: ArrayList<String> = arrayListOf()
        for (child in linear_layout_formats.children) {
            if (child.isSelected) formats.add("${child.tag}")
        }

        val minScore = (rating_bar_min.rating * 2).toDouble()
        val maxScore = (rating_bar_max.rating * 2).toDouble()

        val minReleaseDate = Constants.stringToDate(edit_text_release_date_min.text.toString(), sharedPrefHandler)
        val maxReleaseDate = Constants.stringToDate(edit_text_release_date_max.text.toString(), sharedPrefHandler)

        val minPurchaseDate = Constants.stringToDate(edit_text_purchase_date_min.text.toString(), sharedPrefHandler)
        val maxPurchaseDate = Constants.stringToDate(edit_text_purchase_date_max.text.toString(), sharedPrefHandler)

        var minPrice = 0.0
        try {
            minPrice = edit_text_price_min.text.toString().toDouble()
        } catch (e: Exception){}
        var maxPrice = 0.0
        try {
            maxPrice = edit_text_price_max.text.toString().toDouble()
        } catch (e: Exception){}

        val isGoty = radio_button_goty_yes.isChecked

        val isLoaned = radio_button_loaned_yes.isChecked

        val hasSaga = radio_button_saga_yes.isChecked

        val hasSongs = radio_button_songs_yes.isChecked

        val filters = FilterModel(
            platforms,
            genres,
            formats,
            minScore,
            maxScore,
            minReleaseDate,
            maxReleaseDate,
            minPurchaseDate,
            maxPurchaseDate,
            minPrice,
            maxPrice,
            isGoty,
            isLoaned,
            hasSaga,
            hasSongs
        )

        if (
            platforms.isEmpty() &&
            genres.isEmpty() &&
            formats.isEmpty() &&
            minScore == 0.0 &&
            maxScore == 10.0 &&
            minReleaseDate == null &&
            maxReleaseDate == null &&
            minPurchaseDate == null &&
            maxPurchaseDate == null &&
            minPrice == 0.0 &&
            maxPrice == 0.0 &&
            !isGoty &&
            !isLoaned &&
            !hasSaga &&
            !hasSongs) {
            currentFilters = null
        } else {
            currentFilters = filters
        }

        onFiltersSelected.filter(currentFilters)
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

interface OnFiltersSelected {
    fun filter(filters: FilterModel?)
}
