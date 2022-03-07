package es.upsa.mimo.gamercollection.fragments.popups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnFiltersSelected
import es.upsa.mimo.gamercollection.databinding.FragmentPopupFilterDialogBinding
import es.upsa.mimo.gamercollection.extensions.addChip
import es.upsa.mimo.gamercollection.extensions.toDate
import es.upsa.mimo.gamercollection.extensions.toString
import es.upsa.mimo.gamercollection.models.FilterModel
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.PopupFilterViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.PopupFilterViewModel

class PopupFilterDialogFragment(
    private var currentFilters: FilterModel?,
    private val onFiltersSelected: OnFiltersSelected
) : DialogFragment() {

    //region Private properties
    private lateinit var binding: FragmentPopupFilterDialogBinding
    private lateinit var viewModel: PopupFilterViewModel
    //endregion

    //region Lifecycle methods
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
    //endregion

    //region Public methods
    fun cancel() {
        dismiss()
    }

    fun reset() {

        for (child in binding.chipGroupPlatforms.children) {
            (child as Chip).isChecked = false
        }

        for (child in binding.chipGroupGenres.children) {
            (child as Chip).isChecked = false
        }

        for (child in binding.chipGroupFormats.children) {
            (child as Chip).isChecked = false
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
        for (childId in binding.chipGroupPlatforms.checkedChipIds) {
            binding.chipGroupPlatforms.children.find { child ->
                child.id == childId
            }?.tag?.let { tag ->
                platforms.add("$tag")
            }
        }

        val genres: ArrayList<String> = arrayListOf()
        for (childId in binding.chipGroupGenres.checkedChipIds) {
            binding.chipGroupGenres.children.find { child ->
                child.id == childId
            }?.tag?.let { tag ->
                genres.add("$tag")
            }
        }

        val formats: ArrayList<String> = arrayListOf()
        for (childId in binding.chipGroupFormats.checkedChipIds) {
            binding.chipGroupFormats.children.find { child ->
                child.id == childId
            }?.tag?.let { tag ->
                formats.add("$tag")
            }
        }

        val minScore = (binding.ratingBarMin.rating * 2).toDouble()
        val maxScore = (binding.ratingBarMax.rating * 2).toDouble()

        val minReleaseDate = binding.customEditTextReleaseDateMin.getText().toDate(
            viewModel.filterDateFormat,
            viewModel.language
        )
        val maxReleaseDate = binding.customEditTextReleaseDateMax.getText().toDate(
            viewModel.filterDateFormat,
            viewModel.language
        )

        val minPurchaseDate = binding.customEditTextPurchaseDateMin.getText().toDate(
            viewModel.filterDateFormat,
            viewModel.language
        )
        val maxPurchaseDate = binding.customEditTextPurchaseDateMax.getText().toDate(
            viewModel.filterDateFormat,
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
    //endregion

    //region Description
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            PopupFilterViewModelFactory(application)
        )[PopupFilterViewModel::class.java]

        fillPlatforms()
        fillGenres()
        fillFormats()

        binding.customEditTextReleaseDateMin.setDatePickerFormat(
            requireActivity(),
            viewModel.filterDateFormat
        )
        binding.customEditTextReleaseDateMax.setDatePickerFormat(
            requireActivity(),
            viewModel.filterDateFormat
        )

        binding.customEditTextPurchaseDateMin.setDatePickerFormat(
            requireActivity(),
            viewModel.filterDateFormat
        )
        binding.customEditTextPurchaseDateMax.setDatePickerFormat(
            requireActivity(),
            viewModel.filterDateFormat
        )

        configFilters(currentFilters)

        binding.fragment = this
        binding.filter = currentFilters
    }

    private fun configFilters(filters: FilterModel?) {

        filters?.let {

            val platforms = filters.platforms
            if (platforms.isNotEmpty()) {
                for (child in binding.chipGroupPlatforms.children) {
                    (child as Chip).isChecked = platforms.firstOrNull { it == child.tag } != null
                }
            }

            val genres = filters.genres
            if (genres.isNotEmpty()) {
                for (child in binding.chipGroupGenres.children) {
                    (child as Chip).isChecked = genres.firstOrNull { it == child.tag } != null
                }
            }

            val formats = filters.formats
            if (formats.isNotEmpty()) {
                for (child in binding.chipGroupFormats.children) {
                    (child as Chip).isChecked = formats.firstOrNull { it == child.tag } != null
                }
            }

            binding.ratingBarMin.rating = (filters.minScore / 2).toFloat()
            binding.ratingBarMax.rating = (filters.maxScore / 2).toFloat()

            binding.customEditTextReleaseDateMin.setText(
                filters.minReleaseDate.toString(
                    viewModel.filterDateFormat,
                    viewModel.language
                )
            )
            binding.customEditTextReleaseDateMax.setText(
                filters.maxReleaseDate.toString(
                    viewModel.filterDateFormat,
                    viewModel.language
                )
            )

            binding.customEditTextPurchaseDateMin.setText(
                filters.minPurchaseDate.toString(
                    viewModel.filterDateFormat,
                    viewModel.language
                )
            )
            binding.customEditTextPurchaseDateMax.setText(
                filters.maxPurchaseDate.toString(
                    viewModel.filterDateFormat,
                    viewModel.language
                )
            )

            if (filters.minPrice > 0) binding.customEditTextPriceMin.setText(filters.minPrice.toString())
            if (filters.maxPrice > 0) binding.customEditTextPriceMax.setText(filters.maxPrice.toString())
        }
    }

    private fun fillPlatforms() {

        binding.chipGroupPlatforms.removeAllViews()
        for (platform in viewModel.platforms) {
            binding.chipGroupPlatforms.addChip(layoutInflater, platform.id, platform.name)
        }
    }

    private fun fillGenres() {

        binding.chipGroupGenres.removeAllViews()
        for (genre in viewModel.genres) {
            binding.chipGroupGenres.addChip(layoutInflater, genre.id, genre.name)
        }
    }

    private fun fillFormats() {

        binding.chipGroupFormats.removeAllViews()
        for (format in viewModel.formats) {
            binding.chipGroupFormats.addChip(layoutInflater, format.id, format.name)
        }
    }
    //endregion
}
