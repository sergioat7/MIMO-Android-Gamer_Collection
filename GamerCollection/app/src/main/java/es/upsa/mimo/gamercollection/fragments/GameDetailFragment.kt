package es.upsa.mimo.gamercollection.fragments

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ArrayAdapter
import android.widget.RatingBar
import androidx.core.content.ContextCompat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.extensions.showDatePicker
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.persistence.repositories.FormatRepository
import es.upsa.mimo.gamercollection.persistence.repositories.GenreRepository
import es.upsa.mimo.gamercollection.persistence.repositories.PlatformRepository
import kotlinx.android.synthetic.main.fragment_game_detail.*
import kotlinx.android.synthetic.main.set_image_dialog.view.*
import java.util.*

class GameDetailFragment : BaseFragment(), RatingBar.OnRatingBarChangeListener {

    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var menu: Menu
    private var currentGame: GameResponse? = null
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_game_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formatRepository = FormatRepository(requireContext())
        genreRepository = GenreRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        this.menu = menu
        menu.clear()
        inflater.inflate(R.menu.game_details_toolbar_menu, menu)
        menu.findItem(R.id.action_save_game).isVisible = false
        menu.findItem(R.id.action_cancel_game).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_edit_game -> {
                editGame()
                return true
            }
            R.id.action_save_game -> {
                saveGame()
                return true
            }
            R.id.action_cancel_game -> {
                cancelEdition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        text_view_rating.text = rating.toString()
    }

    //MARK: - Private functions

    private fun initializeUI() {

        game_image_view.setOnClickListener { setImage() }
        val platforms = ArrayList<String>()
        platforms.run {
            this.add(resources.getString((R.string.GAME_DETAIL_SELECT_PLATFORM)))
            this.addAll(platformRepository.getPlatforms().mapNotNull { it.name })
        }
        spinner_platforms.adapter = getAdapter(platforms)
        val genres = ArrayList<String>()
        genres.run {
            this.add(resources.getString((R.string.GAME_DETAIL_SELECT_GENRE)))
            this.addAll(genreRepository.getGenres().mapNotNull { it.name })
        }
        spinner_genres.adapter = getAdapter(genres)
        val formats = ArrayList<String>()
        formats.run {
            this.add(resources.getString((R.string.GAME_DETAIL_SELECT_GENRE)))
            this.addAll(formatRepository.getFormats().mapNotNull { it.name })
        }
        spinner_formats.adapter = getAdapter(formats)
        pending_button.setOnClickListener {
            it.isSelected = !it.isSelected
            in_progress_button.isSelected = false
            finished_button.isSelected = false
        }
        in_progress_button.setOnClickListener {

            pending_button.isSelected = false
            it.isSelected = !it.isSelected
            finished_button.isSelected = false
        }
        finished_button.setOnClickListener {

            pending_button.isSelected = false
            in_progress_button.isSelected = false
            it.isSelected = !it.isSelected
        }
        val pegis = resources.getStringArray(R.array.pegis).toList()
        spinner_pegis.adapter = getAdapter(pegis)
        edit_text_release_date.showDatePicker(requireContext())
        rating_bar.onRatingBarChangeListener = this
        edit_text_purchase_date.showDatePicker(requireContext())
        edit_text_saga.setReadOnly(true, InputType.TYPE_NULL, 0)
        button_add_song.setOnClickListener { addSong() }
        button_delete_game.setOnClickListener { deleteGame() }

        enableEdition(true)
    }

    private fun showData(game: GameResponse?) {

        currentGame = game
    }

    private fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color2)

        edit_text_name.setReadOnly(!enable, inputTypeText, backgroundColor)
        game_image_view.isEnabled = enable
        spinner_platforms.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_platforms.isEnabled = enable
        spinner_genres.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_genres.isEnabled = enable
        spinner_formats.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_formats.isEnabled = enable
        edit_text_release_date.setReadOnly(!enable, InputType.TYPE_NULL, backgroundColor)
        rating_bar.setIsIndicator(!enable)
        pending_button.isEnabled = enable
        in_progress_button.isEnabled = enable
        finished_button.isEnabled = enable
        edit_text_distributor.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_developer.setReadOnly(!enable, inputTypeText, backgroundColor)
        spinner_pegis.backgroundTintList = if (!enable) ColorStateList.valueOf(Color.TRANSPARENT) else ColorStateList.valueOf(backgroundColor)
        spinner_pegis.isEnabled = enable
        edit_text_players.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_price.setReadOnly(!enable, if (enable) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_date.setReadOnly(!enable, InputType.TYPE_NULL, backgroundColor)
        edit_text_purchase_location.setReadOnly(!enable, inputTypeText, backgroundColor)
        radio_button_no.isClickable = enable
        radio_button_yes.isClickable = enable
        edit_text_loaned.setReadOnly(!enable, inputTypeText, backgroundColor)
        edit_text_video_url.setReadOnly(!enable, if (enable) InputType.TYPE_TEXT_VARIATION_URI else InputType.TYPE_NULL, backgroundColor)
        edit_text_observations.setReadOnly(!enable, inputTypeText, backgroundColor)
        button_add_song.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun setImage() {

        val dialogBuilder = AlertDialog.Builder(requireContext()).create()
        val dialogView = this.layoutInflater.inflate(R.layout.set_image_dialog, null)

        dialogView.button_accept.setOnClickListener {

            val url = dialogView.edit_text_url.text.toString()
            if (!url.isEmpty()) {

                Picasso.with(requireContext())
                    .load(url)
                    .error(R.drawable.add_photo)
                    .into(game_image_view, object : Callback {
                        override fun onSuccess() {
                            imageUrl = url
                        }
                        override fun onError() {
                            showPopupDialog(resources.getString(R.string.ERROR_IMAGE_URL))
                        }
                    })
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.setView(dialogView)
        dialogBuilder.show()
    }

    private fun addSong() {
    }

    private fun deleteGame() {
    }

    private fun editGame(){

        menu.findItem(R.id.action_edit_game).isVisible = false
        menu.findItem(R.id.action_save_game).isVisible = true
        menu.findItem(R.id.action_cancel_game).isVisible = true
        enableEdition(true)
    }

    private fun saveGame() {
    }

    private fun cancelEdition(){

        menu.findItem(R.id.action_edit_game).isVisible = true
        menu.findItem(R.id.action_save_game).isVisible = false
        menu.findItem(R.id.action_cancel_game).isVisible = false
        showData(currentGame)
        enableEdition(false)
    }

    private fun getAdapter(data: List<String>): ArrayAdapter<String> {

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, data)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        return arrayAdapter
    }
}
