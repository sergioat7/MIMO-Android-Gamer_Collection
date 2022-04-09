/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 9/4/2022
 */

package es.upsa.mimo.gamercollection.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.GameDetailPagerAdapter
import es.upsa.mimo.gamercollection.base.BindingFragment
import es.upsa.mimo.gamercollection.databinding.DialogSetImageBinding
import es.upsa.mimo.gamercollection.databinding.DialogSetRatingBinding
import es.upsa.mimo.gamercollection.databinding.FragmentGameDetailBinding
import es.upsa.mimo.gamercollection.extensions.*
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.CustomDropdownType
import es.upsa.mimo.gamercollection.utils.StatusBarStyle
import es.upsa.mimo.gamercollection.viewmodelfactories.GameDetailViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameDetailViewModel

class GameDetailFragment : BindingFragment<FragmentGameDetailBinding>() {

    //region Protected properties
    override val statusBarStyle = StatusBarStyle.SECONDARY
    //endregion

    //region  - Private properties
    private val args: GameDetailFragmentArgs by navArgs()
    private lateinit var viewModel: GameDetailViewModel
    private var menu: Menu? = null
    private lateinit var pagerAdapter: GameDetailPagerAdapter
    private var game: GameResponse? = null
    private val goBack = MutableLiveData<Boolean>()
    //endregion

    //region Lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        val menuRes =
            if (args.isRawgGame) R.menu.rawg_game_toolbar_menu else R.menu.game_toolbar_menu
        inflater.inflate(menuRes, menu)
        if (!args.isRawgGame) {

            menu.findItem(R.id.action_edit).isVisible = game != null
            menu.findItem(R.id.action_remove).isVisible = game != null
            menu.findItem(R.id.action_save).isVisible = game == null
            menu.findItem(R.id.action_cancel).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
//            android.R.id.home -> {
//                NavUtils.navigateUpFromSameTask(this)
//            }
            R.id.action_edit -> setEdition(true)
            R.id.action_remove -> {

                showPopupConfirmationDialog(resources.getString(R.string.game_detail_delete_confirmation),
                    {
                        viewModel.deleteGame()
                    })
            }
            R.id.action_save -> {

                if (args.isRawgGame || game == null) {
                    viewModel.createGame(getGameData())
                } else {

                    viewModel.setGame(getGameData())
                    setEdition(false)
                }
            }
            R.id.action_cancel -> {

                showData(game)
                setEdition(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region Public methods
    fun setImage() {

        val dialogBinding = DialogSetImageBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(resources.getString(R.string.game_detail_image_modal_title))
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                val url = dialogBinding.textInputLayoutImageUrl.getValue()
                if (url.isNotEmpty()) {
                    binding.imageUrl = url
                }
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun setRating() {

        val dialogBinding = DialogSetRatingBinding.inflate(layoutInflater)
        dialogBinding.rating = binding.ratingButton.text.toString().toDouble() / 2

        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                binding.ratingButton.text = (dialogBinding.ratingBar.rating * 2).toString()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton(resources.getString(R.string.reset)) { _, _ -> }
            .create()
        dialog.show()

        /*
        This is needed to avoid the auto dismiss
         */
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener {
            dialogBinding.rating = binding.ratingButton.text.toString().toDouble() / 2
        }
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        viewModel = ViewModelProvider(
            this, GameDetailViewModelFactory(
                activity?.application,
                args.gameId,
                args.isRawgGame
            )
        )[GameDetailViewModel::class.java]
        setupBindings()

        pagerAdapter = GameDetailPagerAdapter(
            requireActivity(),
            2,
            viewModel.game.value
        )
        binding.viewPagerGame.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerGame) { tab, position ->
            tab.text =
                if (position == 0) resources.getString(R.string.game_detail_title) else resources.getString(
                    R.string.game_detail_songs_title
                )
        }.attach()

        binding.textInputLayoutGameName.setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)
        binding.dropdownTextInputLayoutPlatforms.setHintStyle(R.style.Widget_GamerCollection_TextView_Title_Header)

        binding.activity = this
        binding.isDarkMode = context?.isDarkMode()
    }

    private fun setupBindings() {

        viewModel.gameDetailLoading.observe(viewLifecycleOwner) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.gameDetailSuccessMessage.observe(viewLifecycleOwner) {

            val message = resources.getString(it)
            showPopupDialog(message, goBack)
        }

        viewModel.gameDetailError.observe(viewLifecycleOwner) { error ->

            hideLoading()
            manageError(error)
        }

        viewModel.game.observe(viewLifecycleOwner) {

            game = it
            showData(it)
            makeFieldsEditable(args.isRawgGame || it == null)
        }

        goBack.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun showData(game: GameResponse?) {

        binding.imageUrl = game?.imageUrl

        val image = binding.imageUrl ?: Constants.NO_VALUE
        Picasso
            .get()
            .load(image)
            .into(binding.imageViewBlurred, object : Callback {

                override fun onSuccess() {
                    binding.imageViewBlurred.setBlur(5)
                }

                override fun onError(e: Exception?) {
                }
            })

        binding.imagePegi = requireContext().getImageForPegi(game?.pegi)

        binding.dropdownTextInputLayoutPlatforms.setValue(
            game?.platform,
            CustomDropdownType.PLATFORM
        )

        binding.game = game
        pagerAdapter.showData(game)
    }

    private fun setEdition(editable: Boolean) {

        menu?.let {
            it.findItem(R.id.action_edit).isVisible = !editable
            it.findItem(R.id.action_remove).isVisible = !editable
            it.findItem(R.id.action_save).isVisible = editable
            it.findItem(R.id.action_cancel).isVisible = editable
        }

        makeFieldsEditable(editable)
    }

    private fun makeFieldsEditable(editable: Boolean) {

        binding.editable = editable
        pagerAdapter.setEdition(editable)
    }

    private fun getGameData(): GameResponse {

        val id = args.gameId
        val name = binding.textInputLayoutGameName.getValue()
        val platform =
            Constants.PLATFORMS.firstOrNull { it.name == binding.dropdownTextInputLayoutPlatforms.getValue() }?.id
        val score = binding.ratingButton.text.toString().toDouble()

        return pagerAdapter.getGameData()?.let {

            it.name = name
            it.platform = platform
            it.imageUrl = binding.imageUrl
            it.score = score
            it
        } ?: run {

            GameResponse(
                id,
                name,
                platform,
                score,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null,
                null,
                0.0,
                binding.imageUrl,
                null,
                null,
                null,
                null,
                ArrayList()
            )
        }
    }
    //endregion
}
