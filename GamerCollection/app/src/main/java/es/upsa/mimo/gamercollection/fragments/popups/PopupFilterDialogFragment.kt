package es.upsa.mimo.gamercollection.fragments.popups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnFiltersSelected
import es.upsa.mimo.gamercollection.databinding.FragmentPopupFilterDialogBinding
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.PopupFilterViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.PopupFilterViewModel

class PopupFilterDialogFragment(
    private var currentFilters: FilterModel?,
    private val onFiltersSelected: OnFiltersSelected
) : DialogFragment() {

    //MARK: - Private properties

    private lateinit var binding: FragmentPopupFilterDialogBinding
    private lateinit var viewModel: PopupFilterViewModel

    // MARK: - Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_GamerCollection_DialogTransparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_popup_filter_dialog,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }

    // MARK: - Public methods

    fun cancel() {
        dismiss()
    }

    fun reset() {

        for (child in binding.linearLayoutPlatforms.children) {
            child.isSelected = false
        }

        for (child in binding.linearLayoutGenres.children) {
            child.isSelected = false
        }

        for (child in binding.linearLayoutFormats.children) {
            child.isSelected = false
        }

        binding.ratingBarMin.rating = 0F
        binding.ratingBarMax.rating = 5F

        binding.customEditTextReleaseDateMin.setText(Constants.EMPTY_VALUE)
        binding.customEditTextReleaseDateMax.setText(Constants.EMPTY_VALUE)

        binding.customEditTextPurchaseDateMin.setText(Constants.EMPTY_VALUE)
        binding.customEditTextPurchaseDateMax.setText(Constants.EMPTY_VALUE)

        binding.customEditTextPriceMin.setText(Constants.EMPTY_VALUE)
        binding.customEditTextPriceMax.setText(Constants.EMPTY_VALUE)

        binding.radioButtonGotyNo.isChecked = true

        binding.radioButtonLoanedNo.isChecked = true

        binding.radioButtonSagaNo.isChecked = true

        binding.radioButtonSongsNo.isChecked = true

        currentFilters = null
    }

    fun save() {

        val platforms: ArrayList<String> = arrayListOf()
        for (child in binding.linearLayoutPlatforms.children) {
            if (child.isSelected) platforms.add("${child.tag}")
        }

        val genres: ArrayList<String> = arrayListOf()
        for (child in binding.linearLayoutGenres.children) {
            if (child.isSelected) genres.add("${child.tag}")
        }

        val formats: ArrayList<String> = arrayListOf()
        for (child in binding.linearLayoutFormats.children) {
            if (child.isSelected) formats.add("${child.tag}")
        }

        val minScore = (binding.ratingBarMin.rating * 2).toDouble()
        val maxScore = (binding.ratingBarMax.rating * 2).toDouble()

        val minReleaseDate = Constants.stringToDate(
            binding.customEditTextReleaseDateMin.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )
        val maxReleaseDate = Constants.stringToDate(
            binding.customEditTextReleaseDateMax.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )

        val minPurchaseDate = Constants.stringToDate(
            binding.customEditTextPurchaseDateMin.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )
        val maxPurchaseDate = Constants.stringToDate(
            binding.customEditTextPurchaseDateMax.getText(),
            Constants.getFilterDateFormat(viewModel.language),
            viewModel.language
        )

        var minPrice = 0.0
        try {
            minPrice = binding.customEditTextPriceMin.getText().toDouble()
        } catch (e: Exception) {
        }
        var maxPrice = 0.0
        try {
            maxPrice = binding.customEditTextPriceMax.getText().toDouble()
        } catch (e: Exception) {
        }

        val isGoty = binding.radioButtonGotyYes.isChecked

        val isLoaned = binding.radioButtonLoanedYes.isChecked

        val hasSaga = binding.radioButtonSagaYes.isChecked

        val hasSongs = binding.radioButtonSongsYes.isChecked

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
            !hasSongs
        ) {
            currentFilters = null
        } else {
            currentFilters = filters
        }

        onFiltersSelected.filter(currentFilters)
        dismiss()
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(this, PopupFilterViewModelFactory(application)).get(
            PopupFilterViewModel::class.java
        )

        fillPlatforms()
        fillGenres()
        fillFormats()

        binding.customEditTextReleaseDateMin.setDatePickerFormat(
            Constants.getFilterDateFormat(
                viewModel.language
            )
        )
        binding.customEditTextReleaseDateMax.setDatePickerFormat(
            Constants.getFilterDateFormat(
                viewModel.language
            )
        )

        binding.customEditTextPurchaseDateMin.setDatePickerFormat(
            Constants.getFilterDateFormat(
                viewModel.language
            )
        )
        binding.customEditTextPurchaseDateMax.setDatePickerFormat(
            Constants.getFilterDateFormat(
                viewModel.language
            )
        )

        configFilters(currentFilters)

        binding.fragment = this
        binding.filter = currentFilters
    }

    private fun configFilters(filters: FilterModel?) {

        filters?.let {

            val platforms = filters.platforms
            if (platforms.isNotEmpty()) {
                for (child in binding.linearLayoutPlatforms.children) {
                    child.isSelected = platforms.firstOrNull { it == child.tag } != null
                }
            }

            val genres = filters.genres
            if (genres.isNotEmpty()) {
                for (child in binding.linearLayoutGenres.children) {
                    child.isSelected = genres.firstOrNull { it == child.tag } != null
                }
            }

            val formats = filters.formats
            if (formats.isNotEmpty()) {
                for (child in binding.linearLayoutFormats.children) {
                    child.isSelected = formats.firstOrNull { it == child.tag } != null
                }
            }

            binding.ratingBarMin.rating = (filters.minScore / 2).toFloat()
            binding.ratingBarMax.rating = (filters.maxScore / 2).toFloat()

            binding.customEditTextReleaseDateMin.setText(
                Constants.dateToString(
                    filters.minReleaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )
            binding.customEditTextReleaseDateMax.setText(
                Constants.dateToString(
                    filters.maxReleaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )

            binding.customEditTextPurchaseDateMin.setText(
                Constants.dateToString(
                    filters.minPurchaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )
            binding.customEditTextPurchaseDateMax.setText(
                Constants.dateToString(
                    filters.maxPurchaseDate,
                    Constants.getFilterDateFormat(viewModel.language),
                    viewModel.language
                )
            )

            if (filters.minPrice > 0) binding.customEditTextPriceMin.setText(filters.minPrice.toString())
            if (filters.maxPrice > 0) binding.customEditTextPriceMax.setText(filters.maxPrice.toString())
        }
    }

    private fun fillPlatforms() {

        binding.linearLayoutPlatforms.removeAllViews()
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

            binding.linearLayoutPlatforms.addView(button)
            binding.linearLayoutPlatforms.addView(view)
        }
        binding.linearLayoutPlatforms.removeViewAt(binding.linearLayoutPlatforms.childCount - 1)
    }

    private fun fillGenres() {

        binding.linearLayoutGenres.removeAllViews()
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

            binding.linearLayoutGenres.addView(button)
            binding.linearLayoutGenres.addView(view)
        }
        binding.linearLayoutGenres.removeViewAt(binding.linearLayoutGenres.childCount - 1)
    }

    private fun fillFormats() {

        binding.linearLayoutFormats.removeAllViews()
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

            binding.linearLayoutFormats.addView(button)
            binding.linearLayoutFormats.addView(view)
        }
        binding.linearLayoutFormats.removeViewAt(binding.linearLayoutFormats.childCount - 1)
    }
}
