package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.model.LatLng
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.OnLocationSelected
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.FragmentGameDataBinding
import es.upsa.mimo.gamercollection.extensions.*
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.CustomDropdownType
import es.upsa.mimo.gamercollection.utils.State
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodels.GameDataViewModel

class GameDataFragment(
    private var game: GameResponse? = null,
    private var enabled: Boolean
) : BindingFragment<FragmentGameDataBinding>(), OnLocationSelected {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
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

    fun showData(game: GameResponse?) {

        binding.dropdownTextInputLayoutPegis.setValue(
            game?.pegi,
            CustomDropdownType.PEGI
        )

        game?.state?.let {
            binding.buttonPending.root.isSelected = it == State.PENDING_STATE
            binding.buttonInProgress.root.isSelected = it == State.IN_PROGRESS_STATE
            binding.buttonFinished.root.isSelected = it == State.FINISHED_STATE
        } ?: run {
            binding.buttonFinished.root.isSelected = true
        }

        binding.dropdownTextInputLayoutGenres.setValue(
            game?.genre,
            CustomDropdownType.GENRE
        )

        binding.dropdownTextInputLayoutFormats.setValue(
            game?.format,
            CustomDropdownType.FORMAT
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

    fun getGameData(): GameResponse? {

        val pegi = resources.getStringArray(R.array.pegis)
            .firstOrNull { it == binding.dropdownTextInputLayoutPegis.getValue() }
        val releaseDate = binding.textInputLayoutReleaseDate.getValue().toDate(
            viewModel.dateFormatToShow,
            viewModel.language
        )
        val format =
            Constants.FORMATS.firstOrNull { it.name == binding.dropdownTextInputLayoutFormats.getValue() }?.id
        val genre =
            Constants.GENRES.firstOrNull { it.name == binding.dropdownTextInputLayoutGenres.getValue() }?.id
        val state =
            when {
                binding.buttonPending.root.isSelected -> State.PENDING_STATE
                binding.buttonInProgress.root.isSelected -> State.IN_PROGRESS_STATE
                binding.buttonFinished.root.isSelected -> State.FINISHED_STATE
                else -> null
            }
        val purchaseDate = binding.textInputLayoutPurchaseDate.getValue().toDate(
            viewModel.dateFormatToShow,
            viewModel.language
        )
        val price = try {
            binding.textInputLayoutPrice.getValue().toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }

        return viewModel.getGameData(
            pegi,
            binding.textInputLayoutDistributor.getValue(),
            binding.textInputLayoutDeveloper.getValue(),
            binding.textInputLayoutPlayers.getValue(),
            releaseDate,
            binding.radioButtonYes.isChecked,
            format,
            genre,
            state,
            purchaseDate,
            binding.textInputLayoutPurchaseLocation.getValue(),
            price,
            binding.textInputLayoutVideoUrl.getValue(),
            binding.textInputLayoutLoaned.getValue(),
            binding.textInputLayoutObservations.getValue()
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

            dropdownTextInputLayoutPegis.setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)
            dropdownTextInputLayoutFormats.setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)
            dropdownTextInputLayoutGenres.setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)

            for (view in listOf(
                textInputLayoutDistributor,
                textInputLayoutDeveloper,
                textInputLayoutPlayers,
                textInputLayoutPrice,
                textInputLayoutPurchaseLocation,
                textInputLayoutLoaned,
                textInputLayoutVideoUrl,
                textInputLayoutObservations,
                textInputLayoutSaga
            )) {
                view.setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)
                view.setEndIconOnClickListener {
                    view.textInputEditText.setText("")
                }
            }

            textInputLayoutReleaseDate.apply {
                setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)
                setOnClickListener {
                    this.showDatePicker(requireActivity())
                }
            }

            textInputLayoutPurchaseDate.apply {
                setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)
                setOnClickListener {
                    this.showDatePicker(requireActivity())
                }
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

        viewModel.gameDataLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.gameDataError.observe(viewLifecycleOwner) { error ->

            if (error == null) {
                activity?.finish()
            } else {
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
