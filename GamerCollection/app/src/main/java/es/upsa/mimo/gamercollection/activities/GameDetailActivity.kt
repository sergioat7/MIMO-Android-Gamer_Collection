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

    private lateinit var viewModel: GameDetailViewModel
    private var menu: Menu? = null
    private lateinit var pagerAdapter: GameDetailPagerAdapter
    private var platformValues = ArrayList<String>()
    private var imageUrl: String? = null

    // MARK: - Lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_detail)
        title = Constants.EMPTY_VALUE
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menu = menu
        menu?.let{

            val game = viewModel.game.value
            it.clear()
            menuInflater.inflate(R.menu.game_toolbar_menu, menu)
            it.findItem(R.id.action_edit).isVisible = game != null
            it.findItem(R.id.action_remove).isVisible = game != null
            it.findItem(R.id.action_save).isVisible = game == null
            it.findItem(R.id.action_cancel).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            android.R.id.home -> {

                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            R.id.action_edit -> {

                setEdition(true)
                return true
            }
            R.id.action_remove -> {

                showPopupConfirmationDialog(resources.getString(R.string.game_detail_delete_confirmation)) {
                    viewModel.deleteGame()
                }
                return true
            }
            R.id.action_save -> {

                saveGame()
                return true
            }
            R.id.action_cancel -> {

                setEdition(false)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Private methods

    private fun initializeUI() {

        val id = intent.getIntExtra(Constants.GAME_ID, 0)
        val gameId = if (id > 0) id else null
        viewModel = ViewModelProvider(this, GameDetailViewModelFactory(application, gameId)).get(GameDetailViewModel::class.java)
        setupBindings()

        image_view_game.setOnClickListener { setImage() }
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

                setEdition(false)
                hideLoading()
            }
        })

        viewModel.gameDetailError.observe(this, { error ->

            if (error == null) {
                finish()
            } else {

                hideLoading()
                manageError(error)
            }
        })

        viewModel.game.observe(this, {
            showData(it, it == null)
        })
    }

    private fun showData(game: GameResponse?, enabled: Boolean) {

        val backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)

        var name: String? = null
        var platformPosition = 0

        game?.let {

            imageUrl = game.imageUrl
            imageUrl?.let { url ->

                progress_bar_loading.visibility = View.VISIBLE
                Picasso.get()
                    .load(url)
                    .error(R.drawable.add_photo)
                    .into(image_view_game, object : Callback {
                        override fun onSuccess() {
                            progress_bar_loading.visibility = View.GONE
                        }
                        override fun onError(e: Exception?) {
                            progress_bar_loading.visibility = View.GONE
                        }
                })
                Picasso.get()
                    .load(url)
                    .into(image_view_blurred, object : Callback {
                        override fun onSuccess() {
                            image_view_blurred.setBlur(5)
                        }
                        override fun onError(e: Exception?) {}
                    })
            }

            image_view_goty.visibility = if(game.goty) View.VISIBLE else View.GONE

            val gameName = game.name
            name = if (gameName != null && gameName.isNotBlank()) gameName else if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE

            game.platform?.let { platformId ->
                val platformName = viewModel.platforms.firstOrNull { it.id == platformId }?.name
                val pos = platformValues.indexOf(platformName)
                platformPosition = if(pos > 0) pos else 0
            }

            rating_button.text = game.score.toString()

            image_view_pegi.setImageDrawable(Constants.getPegiImage(game.pegi, this))
        } ?: run {
            name = if (enabled) Constants.EMPTY_VALUE else Constants.NO_VALUE
        }

        custom_edit_text_name.setText(name)
        spinner_platforms.setSelection(platformPosition)
        spinner_platforms.visibility = if (enabled || platformPosition > 0) View.VISIBLE else View.GONE

        image_view_game.isEnabled = enabled
        custom_edit_text_name.setReadOnly(!enabled, backgroundColor)
        spinner_platforms.backgroundTintList = if (!enabled) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_platforms.isEnabled = enabled
        rating_button.isEnabled = enabled
    }

    private fun setImage() {

        val dialogBuilder = AlertDialog.Builder(this).create()
        val dialogView = this.layoutInflater.inflate(R.layout.set_image_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val url = dialogView.custom_edit_text_url.getText()
            if (url.isNotEmpty()) {

                Picasso.get()
                    .load(url)
                    .error(R.drawable.add_photo)
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

        dialogView.rating_bar.rating = rating_button.text.toString().toFloat()
        dialogView.button_rate.setOnClickListener {

            rating_button.text = dialogView.rating_bar.rating.toString()
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun saveGame() {

        val platform = viewModel.platforms.firstOrNull { it.name == spinner_platforms.selectedItem.toString() }?.id

        viewModel.saveGame(
            custom_edit_text_name.getText(),
            platform,
            rating_button.text.toString().toDouble(),
            imageUrl,
            pagerAdapter.getGameData()
        )
    }

    private fun setEdition(value: Boolean) {

        showEditButton(value)
        showData(viewModel.game.value, value)
        pagerAdapter.showData(viewModel.game.value, value)
        pagerAdapter.enableEdition(value)
    }

    private fun showEditButton(hidden: Boolean) {

        menu?.let {
            it.findItem(R.id.action_edit).isVisible = !hidden
            it.findItem(R.id.action_remove).isVisible = !hidden
            it.findItem(R.id.action_save).isVisible = hidden
            it.findItem(R.id.action_cancel).isVisible = hidden
        }
    }
}
