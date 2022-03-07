package es.upsa.mimo.gamercollection.activities

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.adapters.GameDetailPagerAdapter
import es.upsa.mimo.gamercollection.base.BaseActivity
import es.upsa.mimo.gamercollection.databinding.ActivityGameDetailBinding
import es.upsa.mimo.gamercollection.databinding.SetImageDialogBinding
import es.upsa.mimo.gamercollection.extensions.getImageForPegi
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.GameDetailViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameDetailViewModel
import kotlinx.android.synthetic.main.set_rating_dialog.view.*

class GameDetailActivity : BaseActivity() {

    //region  - Private properties
    private var gameId: Int? = null
    private var isRawgGame: Boolean = false
    private lateinit var binding: ActivityGameDetailBinding
    private lateinit var viewModel: GameDetailViewModel
    private var menu: Menu? = null
    private lateinit var pagerAdapter: GameDetailPagerAdapter
    private var game: GameResponse? = null
    private var platformValues = ArrayList<String>()
    private var imageUrl: String? = null
    private val goBack = MutableLiveData<Boolean>()
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_detail)
        setContentView(binding.root)

        title = Constants.EMPTY_VALUE
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra(Constants.GAME_ID, 0)
        gameId = if (id > 0) id else null
        isRawgGame = intent.getBooleanExtra(Constants.IS_RAWG_GAME, false)

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menu = menu
        menu?.let {

            it.clear()
            val menuRes =
                if (isRawgGame) R.menu.rawg_game_toolbar_menu else R.menu.game_toolbar_menu
            menuInflater.inflate(menuRes, menu)
            if (!isRawgGame) {

                menu.findItem(R.id.action_edit).isVisible = game != null
                menu.findItem(R.id.action_remove).isVisible = game != null
                menu.findItem(R.id.action_save).isVisible = game == null
                it.findItem(R.id.action_cancel).isVisible = false
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
            }
            R.id.action_edit -> setEdition(true)
            R.id.action_remove -> {

                showPopupConfirmationDialog(resources.getString(R.string.game_detail_delete_confirmation)) {
                    viewModel.deleteGame()
                }
            }
            R.id.action_save -> {

                if (isRawgGame || game == null) {
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

        val dialogBinding = SetImageDialogBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.game_detail_image_modal_title))
            .setView(dialogBinding.root)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->

                val url = dialogBinding.customEditTextUrl.getText()
                if (url.isNotEmpty()) {

                    Picasso.get()
                        .load(url)
                        .error(R.drawable.ic_add_image)
                        .into(binding.imageViewGame, object : Callback {
                            override fun onSuccess() {
                                imageUrl = url
                            }

                            override fun onError(e: Exception?) {
                                imageUrl = null
                                showPopupDialog(resources.getString(R.string.error_image_url))
                            }
                        })
                }
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun setRating() {

        val dialogBuilder = MaterialAlertDialogBuilder(this).create()
        val dialogView = this.layoutInflater.inflate(R.layout.set_rating_dialog, null)

        dialogView.rating_bar.rating = binding.ratingButton.text.toString().toFloat() / 2
        dialogView.button_rate.setOnClickListener {

            binding.ratingButton.text = (dialogView.rating_bar.rating * 2).toString()
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        viewModel = ViewModelProvider(
            this, GameDetailViewModelFactory(
                application,
                gameId,
                isRawgGame
            )
        )[GameDetailViewModel::class.java]
        setupBindings()

        platformValues = ArrayList()
        platformValues.run {
            this.add(resources.getString((R.string.game_detail_select_platform)))
            this.addAll(viewModel.platforms.map { it.name })
        }
        binding.spinnerPlatforms.adapter = Constants.getAdapter(this, platformValues)

        pagerAdapter = GameDetailPagerAdapter(
            this,
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

        binding.activity = this
    }

    private fun setupBindings() {

        viewModel.gameDetailLoading.observe(this) { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        viewModel.gameDetailSuccessMessage.observe(this) {

            val message = resources.getString(it)
            showPopupDialog(message, goBack)
        }

        viewModel.gameDetailError.observe(this) { error ->

            hideLoading()
            manageError(error)
        }

        viewModel.game.observe(this) {

            game = it
            showData(it)
            makeFieldsEditable(isRawgGame || it == null)
        }

        goBack.observe(this) {
            finish()
        }
    }

    private fun showData(game: GameResponse?) {

        imageUrl = game?.imageUrl

        val image = imageUrl ?: Constants.NO_VALUE
        binding.progressBarLoading.visibility = View.VISIBLE
        Picasso
            .get()
            .load(image)
            .error(R.drawable.ic_add_image)
            .into(binding.imageViewGame, object : Callback {

                override fun onSuccess() {
                    binding.progressBarLoading.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progressBarLoading.visibility = View.GONE
                }
            })
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

        binding.imagePegi = this.getImageForPegi(game?.pegi)

        val name = game?.name ?: Constants.EMPTY_VALUE
        binding.customEditTextName.setText(name.ifBlank { Constants.NO_VALUE })

        var platformPosition = 0
        game?.platform?.let { platformId ->

            val platformName = viewModel.platforms.firstOrNull { it.id == platformId }?.name
            val pos = platformValues.indexOf(platformName)
            platformPosition = if (pos > 0) pos else 0
        }
        binding.spinnerPlatforms.setSelection(platformPosition)

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

        val backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)

        binding.customEditTextName.setReadOnly(!editable, backgroundColor)
        binding.spinnerPlatforms.visibility =
            if (editable || binding.spinnerPlatforms.selectedItemPosition > 0) View.VISIBLE
            else View.GONE
        binding.spinnerPlatforms.backgroundTintList =
            if (!editable) ColorStateList.valueOf(Color.TRANSPARENT)
            else ColorStateList.valueOf(backgroundColor)

        binding.editable = editable
        pagerAdapter.setEdition(editable)
    }

    private fun getGameData(): GameResponse {

        val id = gameId ?: 0
        val name = binding.customEditTextName.getText()
        val platform =
            viewModel.platforms.firstOrNull { it.name == binding.spinnerPlatforms.selectedItem.toString() }?.id
        val score = binding.ratingButton.text.toString().toDouble()

        return pagerAdapter.getGameData()?.let {

            it.name = name
            it.platform = platform
            it.imageUrl = imageUrl
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
                imageUrl,
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
