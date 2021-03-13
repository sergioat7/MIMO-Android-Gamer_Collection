package es.upsa.mimo.gamercollection.activities

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.base.BaseActivity
import es.upsa.mimo.gamercollection.adapters.GameDetailPagerAdapter
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.viewmodelfactories.GameDetailViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.GameDetailViewModel
import kotlinx.android.synthetic.main.activity_game_detail.*
import kotlinx.android.synthetic.main.set_image_dialog.view.*
import kotlinx.android.synthetic.main.set_rating_dialog.view.*

class GameDetailActivity : BaseActivity() {

    //MARK: - Private properties

    private var gameId: Int? = null
    private var isRawgGame: Boolean = false
    private lateinit var viewModel: GameDetailViewModel
    private var menu: Menu? = null
    private lateinit var pagerAdapter: GameDetailPagerAdapter
    private var game: GameResponse? = null
    private var platformValues = ArrayList<String>()
    private var imageUrl: String? = null
    private val goBack = MutableLiveData<Boolean>()

    // MARK: - Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_detail)
        title = Constants.EMPTY_VALUE
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra(Constants.GAME_ID, 0)
        gameId = if (id > 0) id else null
        isRawgGame = intent.getBooleanExtra(Constants.IS_RAWG_GAME, false)

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menu = menu
        menu?.let{

            it.clear()
            val menuRes = if (isRawgGame) R.menu.rawg_game_toolbar_menu else R.menu.game_toolbar_menu
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

        when(item.itemId) {
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

    //MARK: - Private methods

    private fun initializeUI() {

        viewModel = ViewModelProvider(this, GameDetailViewModelFactory(
            application,
            gameId,
            isRawgGame)
        ).get(GameDetailViewModel::class.java)
        setupBindings()

        image_view_game.setOnClickListener {
            setImage()
        }
        platformValues = ArrayList()
        platformValues.run {
            this.add(resources.getString((R.string.game_detail_select_platform)))
            this.addAll(viewModel.platforms.map { it.name })
        }
        spinner_platforms.adapter = Constants.getAdapter(this, platformValues)

        rating_button.setOnClickListener { setRating() }

        pagerAdapter = GameDetailPagerAdapter(
            this,
            2,
            viewModel.game.value
        )
        viewPager2.adapter = pagerAdapter
        TabLayoutMediator(tab_layout, viewPager2) { tab, position ->
            tab.text = if(position == 0) resources.getString(R.string.game_detail_title) else resources.getString(R.string.game_detail_songs_title)
        }.attach()
    }

    private fun setupBindings() {

        viewModel.gameDetailLoading.observe(this, { isLoading ->

            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })

        viewModel.gameDetailSuccessMessage.observe(this, {

            val message = resources.getString(it)
            showPopupDialog(message, goBack)
        })

        viewModel.gameDetailError.observe(this, { error ->

            hideLoading()
            manageError(error)
        })

        viewModel.game.observe(this, {

            game = it
            showData(it)
            makeFieldsEditable(isRawgGame || it == null)
        })

        goBack.observe(this, {
            finish()
        })
    }

    private fun showData(game: GameResponse?) {

        imageUrl = game?.imageUrl

        val image = imageUrl ?: Constants.NO_VALUE
        val errorImage = if (Constants.isDarkMode(this)) R.drawable.ic_add_image_light else R.drawable.ic_add_image_dark
        progress_bar_loading.visibility = View.VISIBLE
        Picasso
            .get()
            .load(image)
            .error(errorImage)
            .into(image_view_game, object : Callback {

                override fun onSuccess() {
                    progress_bar_loading.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progress_bar_loading.visibility = View.GONE
                }
            })
        Picasso
            .get()
            .load(image)
            .into(image_view_blurred, object : Callback {

                override fun onSuccess() {
                    image_view_blurred.setBlur(5)
                }

                override fun onError(e: Exception?) {
                }
            })

        image_view_goty.visibility = if(game?.goty == true) View.VISIBLE else View.GONE

        rating_button.text = (game?.score ?: 0).toString()

        image_view_pegi.setImageDrawable(Constants.getPegiImage(game?.pegi, this))

        val name = game?.name ?: Constants.EMPTY_VALUE
        custom_edit_text_name.setText(
            if (name.isNotBlank()) name
            else Constants.NO_VALUE
        )

        var platformPosition = 0
        game?.platform?.let { platformId ->

            val platformName = viewModel.platforms.firstOrNull { it.id == platformId }?.name
            val pos = platformValues.indexOf(platformName)
            platformPosition = if(pos > 0) pos else 0
        }
        spinner_platforms.setSelection(platformPosition)

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

        spinner_platforms.visibility =
            if (editable || spinner_platforms.selectedItemPosition > 0) View.VISIBLE
            else View.GONE
        image_view_game.isEnabled = editable
        custom_edit_text_name.setReadOnly(!editable, backgroundColor)
        spinner_platforms.backgroundTintList =
            if (!editable) ColorStateList.valueOf(Color.TRANSPARENT)
            else ColorStateList.valueOf(backgroundColor)
        spinner_platforms.isEnabled = editable
        rating_button.isEnabled = editable

        pagerAdapter.setEdition(editable)
    }

    private fun setImage() {

        val dialogBuilder = AlertDialog.Builder(this).create()
        val dialogView = this.layoutInflater.inflate(R.layout.set_image_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val url = dialogView.custom_edit_text_url.getText()
            val errorImage = if (Constants.isDarkMode(this)) R.drawable.ic_add_image_light else R.drawable.ic_add_image_dark
            if (url.isNotEmpty()) {

                Picasso.get()
                    .load(url)
                    .error(errorImage)
                    .into(image_view_game, object : Callback {
                        override fun onSuccess() {
                            imageUrl = url
                        }
                        override fun onError(e: Exception?) {
                            imageUrl = null
                            showPopupDialog(resources.getString(R.string.error_image_url))
                        }
                    })
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun setRating() {

        val dialogBuilder = AlertDialog.Builder(this).create()
        val dialogView = this.layoutInflater.inflate(R.layout.set_rating_dialog, null)

        dialogView.rating_bar.rating = rating_button.text.toString().toFloat() / 2
        dialogView.button_rate.setOnClickListener {

            rating_button.text = (dialogView.rating_bar.rating * 2).toString()
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun getGameData(): GameResponse {

        val id = gameId ?: 0
        val name = custom_edit_text_name.getText()
        val platform = viewModel.platforms.firstOrNull { it.name == spinner_platforms.selectedItem.toString() }?.id
        val score = rating_button.text.toString().toDouble()

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
                ArrayList())
        }
    }
}
