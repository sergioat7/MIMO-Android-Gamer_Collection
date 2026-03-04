package es.upsa.mimo.gamercollection.presentation.gamedetail.gamedata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.data.remote.model.FORMATS
import es.upsa.mimo.gamercollection.data.remote.model.GENRES
import es.upsa.mimo.gamercollection.databinding.FragmentGameDataBinding
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.extensions.getValue
import es.upsa.mimo.gamercollection.extensions.getValueWithoutHyphen
import es.upsa.mimo.gamercollection.extensions.setEndIconOnClickListener
import es.upsa.mimo.gamercollection.extensions.setHintStyle
import es.upsa.mimo.gamercollection.extensions.setOnClickListener
import es.upsa.mimo.gamercollection.extensions.setValue
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.extensions.toDate
import es.upsa.mimo.gamercollection.interfaces.OnLocationSelected
import es.upsa.mimo.gamercollection.presentation.base.BindingFragment
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.CustomDropdownType
import es.upsa.mimo.gamercollection.utils.State
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class GameDataFragment(
    private var game: Game? = null,
    private var enabled: Boolean,
) : BindingFragment<FragmentGameDataBinding>(), OnLocationSelected {

    //region Protected properties
    override val statusBarStyle = null
    override val hasOptionsMenu = false
    //endregion

    //region Private properties
    private val viewModel = GameDataViewModel(game)
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUi()
    }
    //endregion

    //region Public methods
    override fun setLocation(location: LatLng?) {
        var locationText = Constants.EMPTY_VALUE
        location?.let {
            locationText = "${it.latitude},${it.longitude}"
        }
        binding.textInputLayoutPurchaseLocation.text = locationText
    }

    fun showData(game: Game?) {
        binding.dropdownTextInputLayoutPegis.setValue(
            game?.pegi,
            CustomDropdownType.PEGI,
        )

        game?.state?.let {
            binding.buttonPending.root.isSelected = it == State.PENDING_STATE
            binding.buttonInProgress.root.isSelected = it == State.IN_PROGRESS_STATE
            binding.buttonFinished.root.isSelected = it == State.FINISHED_STATE
        } ?: run {
            binding.buttonPending.root.isSelected = true
        }

        binding.dropdownTextInputLayoutGenres.setValue(
            game?.genre,
            CustomDropdownType.GENRE,
        )

        binding.dropdownTextInputLayoutFormats.setValue(
            game?.format,
            CustomDropdownType.FORMAT,
        )

        binding.game = game
    }

    fun setEdition(editable: Boolean) {
        enabled = editable

        binding.buttonPending.root.isEnabled = editable
        binding.buttonInProgress.root.isEnabled = editable
        binding.buttonFinished.root.isEnabled = editable

        binding.editable = editable
    }

    fun getGameData(): Game? {
        val pegi = resources
            .getStringArray(R.array.pegis)
            .firstOrNull { it == binding.dropdownTextInputLayoutPegis.getValue() }
        val releaseDate = binding.textInputLayoutReleaseDate.getValueWithoutHyphen().toDate(
            viewModel.dateFormatToShow,
            viewModel.language,
        )
        val format =
            FORMATS.firstOrNull { it.name == binding.dropdownTextInputLayoutFormats.getValue() }?.id
        val genre =
            GENRES.firstOrNull { it.name == binding.dropdownTextInputLayoutGenres.getValue() }?.id
        val state =
            when {
                binding.buttonPending.root.isSelected -> State.PENDING_STATE
                binding.buttonInProgress.root.isSelected -> State.IN_PROGRESS_STATE
                binding.buttonFinished.root.isSelected -> State.FINISHED_STATE
                else -> null
            }
        val purchaseDate = binding.textInputLayoutPurchaseDate.getValueWithoutHyphen().toDate(
            viewModel.dateFormatToShow,
            viewModel.language,
        )
        val price = try {
            binding.textInputLayoutPrice.getValueWithoutHyphen().toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }

        return viewModel.getGameData(
            pegi,
            binding.textInputLayoutDistributor.getValueWithoutHyphen(),
            binding.textInputLayoutDeveloper.getValueWithoutHyphen(),
            binding.textInputLayoutPlayers.getValueWithoutHyphen(),
            releaseDate,
            binding.radioButtonYes.isChecked,
            format,
            genre,
            state,
            purchaseDate,
            binding.textInputLayoutPurchaseLocation.getValueWithoutHyphen(),
            price,
            binding.textInputLayoutVideoUrl.getValueWithoutHyphen(),
            binding.textInputLayoutLoaned.getValueWithoutHyphen(),
            binding.textInputLayoutObservations.getValueWithoutHyphen(),
        )
    }

    fun buttonClicked(it: View) {
        binding.buttonPending.root.isSelected =
            if (it == binding.buttonPending.root) !it.isSelected else false
        binding.buttonInProgress.root.isSelected =
            if (it == binding.buttonInProgress.root) !it.isSelected else false
        binding.buttonFinished.root.isSelected =
            if (it == binding.buttonFinished.root) !it.isSelected else false
    }
    //endregion

    //region Protected methods
    override fun initializeUi() {
        super.initializeUi()

        setupBindings()

        with(binding) {
            dropdownTextInputLayoutPegis.setHintStyle(
                R.style.Widget_GamerCollection_TextView_Title_Header,
            )
            dropdownTextInputLayoutFormats.setHintStyle(
                R.style.Widget_GamerCollection_TextView_Title_Header,
            )
            dropdownTextInputLayoutGenres.setHintStyle(
                R.style.Widget_GamerCollection_TextView_Title_Header,
            )

            for (view in listOf(
                textInputLayoutReleaseDate,
                textInputLayoutDistributor,
                textInputLayoutDeveloper,
                textInputLayoutPlayers,
                textInputLayoutPrice,
                textInputLayoutPurchaseDate,
                textInputLayoutPurchaseLocation,
                textInputLayoutLoaned,
                textInputLayoutVideoUrl,
                textInputLayoutObservations,
                textInputLayoutSaga,
            )) {
                view.setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)
                view.setEndIconOnClickListener {
                    view.textInputEditText.setText("")
                }
            }

            textInputLayoutReleaseDate.setOnClickListener {
                textInputLayoutReleaseDate.showDatePicker(
                    requireActivity(),
                    viewModel.dateFormatToShow,
                )
            }

            textInputLayoutPurchaseDate.setOnClickListener {
                textInputLayoutPurchaseDate.showDatePicker(
                    requireActivity(),
                    viewModel.dateFormatToShow,
                )
            }

            textInputLayoutPurchaseLocation.setOnClickListener {
                showMap()
            }

            fragment = this@GameDataFragment
        }
        showData(game)
        setEdition(enabled)
    }
    //endregion

    //region Private methods
    private fun setupBindings() {
        lifecycleScope.launch {
            viewModel.gameDataLoading.filterNotNull().collect { isLoading ->

                if (isLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.gameDataError.filterNotNull().collect { error ->
                manageError(error)
            }
        }
    }

    private fun showMap() {
        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("mapDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        var location: LatLng? = null
        val purchaseLocation = game?.purchaseLocation
        if (purchaseLocation != null && purchaseLocation.isNotEmpty()) {
            val latLng = purchaseLocation.split(",")
            location = LatLng(latLng[0].toDouble(), latLng[1].toDouble())
        }

        val dialogFragment = MapsFragment(location, this)
        dialogFragment.show(ft, "mapDialog")
    }
    //endregion
}
