package es.upsa.mimo.gamercollection.activities

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.base.BaseActivity
import es.upsa.mimo.gamercollection.adapters.GameDetailPagerAdapter
import es.upsa.mimo.gamercollection.adapters.SpinnerAdapter
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.PlatformResponse
import es.upsa.mimo.gamercollection.network.apiClient.GameAPIClient
import es.upsa.mimo.gamercollection.network.apiClient.SongAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.FormatRepository
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.GenreRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.activity_game_detail.*
import kotlinx.android.synthetic.main.set_image_dialog.view.*

class GameDetailActivity : BaseActivity() {

    private var gameId: Int? = null
    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var gameRepository: GameRepository
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var songAPIClient: SongAPIClient
    private var menu: Menu? = null
    private lateinit var pagerAdapter: GameDetailPagerAdapter
    private lateinit var platforms: List<PlatformResponse>
    private var platformValues = ArrayList<String>()
    private var currentGame: GameResponse? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_detail)
        title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val gameId = intent.getIntExtra("gameId", 0)
        if (gameId > 0) this.gameId = gameId

        sharedPrefHandler = SharedPreferencesHandler(this)
        formatRepository = FormatRepository(this)
        genreRepository = GenreRepository(this)
        platformRepository = PlatformRepository(this)
        gameRepository = GameRepository(this)
        gameAPIClient = GameAPIClient(resources, sharedPrefHandler)
        songAPIClient = SongAPIClient(resources, sharedPrefHandler)

        initializeUI()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        this.menu = menu
        menu?.let{
            it.clear()
            menuInflater.inflate(R.menu.game_toolbar_menu, menu)
            it.findItem(R.id.action_edit).isVisible = currentGame != null
            it.findItem(R.id.action_save).isVisible = currentGame == null
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
                editGame()
                return true
            }
            R.id.action_save -> {
                saveGame()
                return true
            }
            R.id.action_cancel -> {
                cancelEdition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // MARK: Private functions

    private fun initializeUI() {

        image_view_game.setOnClickListener { setImage() }
        platforms = platformRepository.getPlatforms()
        platformValues = ArrayList<String>()
        platformValues.run {
            this.add(resources.getString((R.string.GAME_DETAIL_SELECT_PLATFORM)))
            this.addAll(platforms.mapNotNull { it.name })
        }
        spinner_platforms.adapter = Constants.getAdapter(this, platformValues)

//        rating_bar.onRatingBarChangeListener = this TODO
    }

    private fun loadData() {

        gameId?.let {

            showLoading()
            currentGame = gameRepository.getGame(it)
            hideLoading()
        }

        pagerAdapter = GameDetailPagerAdapter(this, 2, currentGame)
        viewPager2.adapter = pagerAdapter
        TabLayoutMediator(tab_layout, viewPager2) { tab, position ->
            tab.text = if(position == 0) resources.getString(R.string.GAME_DETAIL_TITLE) else resources.getString(R.string.GAME_DETAIL_SONGS_TITLE)
        }.attach()

        showData(currentGame)
        enableEdition(currentGame == null)
    }

    private fun showData(game: GameResponse?) {

        currentGame = game
        game?.let { game ->

            imageUrl = game.imageUrl
            imageUrl?.let { url ->
                Picasso.with(this).load(url).error(R.drawable.add_photo).into(image_view_game)
                Picasso.with(this).load(url).into(image_view_blurred, object : Callback {
                        override fun onSuccess() {
                            image_view_blurred.setBlur(5)
                        }
                        override fun onError() {}
                    })
            }

            edit_text_name.setText(game.name)
            game.platform?.let { platformId ->
                val platformName = platformRepository.getPlatforms().firstOrNull { it.id == platformId }?.name
                val pos = platformValues.indexOf(platformName)
                spinner_platforms.setSelection( if(pos > 0) pos else 0 )
            }
            rating_button.setText(game.score.toString())
            when(game.pegi) {
                "+3" -> image_view_pegi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pegi3))
                "+4" -> image_view_pegi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pegi4))
                "+6" -> image_view_pegi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pegi6))
                "+7" -> image_view_pegi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pegi7))
                "+12" -> image_view_pegi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pegi12))
                "+16" -> image_view_pegi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pegi16))
                "+18" -> image_view_pegi.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pegi18))
                else -> image_view_pegi.setImageDrawable(null)
            }

            image_view_goty.visibility = if(game.goty) View.VISIBLE else View.GONE

        }
    }

    private fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(this, R.color.color2)

        edit_text_name.setReadOnly(!enable, inputTypeText, backgroundColor)
        image_view_game.isEnabled = enable
        spinner_platforms.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_platforms.isEnabled = enable
//        rating_bar.setIsIndicator(!enable) TODO
    }

    private fun setImage() {

        val dialogBuilder = AlertDialog.Builder(this).create()
        val dialogView = this.layoutInflater.inflate(R.layout.set_image_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val url = dialogView.edit_text_url.text.toString()
            if (!url.isEmpty()) {

                Picasso.with(this)
                    .load(url)
                    .error(R.drawable.add_photo)
                    .into(image_view_game, object : Callback {
                        override fun onSuccess() {
                            imageUrl = url
                        }
                        override fun onError() {
                            imageUrl = null
                            showPopupDialog(resources.getString(R.string.ERROR_IMAGE_URL))
                        }
                    })
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun editGame(){

        showEditButton(true)
        enableEdition(true)
        pagerAdapter.enableEdition(true)
    }

    private fun saveGame() {

        pagerAdapter.getGameData()?.let { game ->

            game.name = edit_text_name.text.toString()
            game.platform = platforms.firstOrNull { it.name == spinner_platforms.selectedItem.toString() }?.id
            game.imageUrl = imageUrl
            game.score = rating_button.text.toString().toDouble()

            showLoading()
            if (currentGame != null) {

                gameAPIClient.setGame(game, {
                    gameRepository.updateGame(it)

                    currentGame = it
                    cancelEdition()
                    hideLoading()
                }, {
                    manageError(it)
                })
            } else {

                gameAPIClient.createGame(game, {
                    gameAPIClient.getGames({ games ->

                        for (game in games) {
                            gameRepository.insertGame(game)
                        }
                        hideLoading()
                        finish()
                    }, {
                        manageError(it)
                    })
                }, {
                    manageError(it)
                })
            }
        }
    }

    private fun cancelEdition(){

        showEditButton(false)
        showData(currentGame)
        pagerAdapter.showData(currentGame)
        enableEdition(false)
        pagerAdapter.enableEdition(false)
    }

    private fun showEditButton(hidden: Boolean) {

        menu?.let {
            it.findItem(R.id.action_edit).isVisible = !hidden
            it.findItem(R.id.action_save).isVisible = hidden
            it.findItem(R.id.action_cancel).isVisible = hidden
        }
    }
}
