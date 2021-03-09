package es.upsa.mimo.gamercollection.fragments.popups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnFiltersSelected
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.PopupFilterViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.PopupFilterViewModel
import kotlinx.android.synthetic.main.fragment_popup_filter_dialog.*

class PopupFilterDialogFragment(
    private var currentFilters: FilterModel?,
    private val onFiltersSelected: OnFiltersSelected
) : DialogFragment() {

    //MARK: - Private properties

    private lateinit var viewModel: PopupFilterViewModel

    // MARK: - Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_GamerCollection_DialogTransparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popup_filter_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, PopupFilterViewModelFactory(application)).get(PopupFilterViewModel::class.java)

        fillPlatforms()
        fillGenres()
        fillFormats()

        custom_edit_text_release_date_min.setDatePickerFormat(Constants.getFilterDateFormat(viewModel.language))
        custom_edit_text_release_date_max.setDatePickerFormat(Constants.getFilterDateFormat(viewModel.language))

        custom_edit_text_purchase_date_min.setDatePickerFormat(Constants.getFilterDateFormat(viewModel.language))
        custom_edit_text_purchase_date_max.setDatePickerFormat(Constants.getFilterDateFormat(viewModel.language))

        button_cancel.setOnClickListener { cancel() }
        button_reset.setOnClickListener { reset() }
        button_save.setOnClickListener { save() }

        configFilters(currentFilters)
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

            custom_edit_text_release_date_min.setText(
                Constants.dateToString(
                    filters.minReleaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )
            custom_edit_text_release_date_max.setText(
                Constants.dateToString(
                    filters.maxReleaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )

            custom_edit_text_purchase_date_min.setText(
                Constants.dateToString(
                    filters.minPurchaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )
            custom_edit_text_purchase_date_max.setText(
                Constants.dateToString(
                    filters.maxPurchaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )

            if (filters.minPrice > 0) custom_edit_text_price_min.setText(filters.minPrice.toString())
            if (filters.maxPrice > 0) custom_edit_text_price_max.setText(filters.maxPrice.toString())

            radio_button_goty_yes.isChecked = filters.isGoty

            radio_button_loaned_yes.isChecked = filters.isLoaned

            radio_button_saga_yes.isChecked = filters.hasSaga

            radio_button_songs_yes.isChecked = filters.hasSongs
        }
    }

    private fun fillPlatforms() {

        linear_layout_platforms.removeAllViews()
        for (platform in viewModel.platforms) {

            val button = viewModel.getRoundedSelectorButton(
                platform.id,
                platform.name,
                requireContext()
            )

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
        for (genre in viewModel.genres) {

            val button = viewModel.getRoundedSelectorButton(
                genre.id,
                genre.name,
                requireContext()
            )

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
        for (format in viewModel.formats) {

            val button = viewModel.getRoundedSelectorButton(
                format.id,
                format.name,
                requireContext()
            )

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

        custom_edit_text_release_date_min.setText(Constants.EMPTY_VALUE)
        custom_edit_text_release_date_max.setText(Constants.EMPTY_VALUE)

        custom_edit_text_purchase_date_min.setText(Constants.EMPTY_VALUE)
        custom_edit_text_purchase_date_max.setText(Constants.EMPTY_VALUE)

        custom_edit_text_price_min.setText(Constants.EMPTY_VALUE)
        custom_edit_text_price_max.setText(Constants.EMPTY_VALUE)

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

        val minReleaseDate = Constants.stringToDate(
            custom_edit_text_release_date_min.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )
        val maxReleaseDate = Constants.stringToDate(
            custom_edit_text_release_date_max.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )

        val minPurchaseDate = Constants.stringToDate(
            custom_edit_text_purchase_date_min.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )
        val maxPurchaseDate = Constants.stringToDate(
            custom_edit_text_purchase_date_max.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )

        var minPrice = 0.0
        try {
            minPrice = custom_edit_text_price_min.getText().toDouble()
        } catch (e: Exception){}
        var maxPrice = 0.0
        try {
            maxPrice = custom_edit_text_price_max.getText().toDouble()
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
}
