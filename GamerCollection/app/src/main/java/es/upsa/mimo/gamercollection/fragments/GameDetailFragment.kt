package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.core.content.ContextCompat
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_game_detail.*

class GameDetailFragment : BaseFragment(), RatingBar.OnRatingBarChangeListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
    }

    //MARK: - Private functions

    private fun initializeUI() {

        game_image_view.setOnClickListener { setImage() }

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

        rating_bar.onRatingBarChangeListener = this
        edit_text_saga.setReadOnly(true, InputType.TYPE_NULL, 0)
        button_add_song.setOnClickListener { addSong() }
        button_delete_game.setOnClickListener { deleteGame() }

        enableEdition(true)
    }

    private fun enableEdition(enable: Boolean) {

        val inputTypeText = if (enable) InputType.TYPE_CLASS_TEXT else InputType.TYPE_NULL
        val editTextBackgroundColor = ContextCompat.getColor(requireContext(), R.color.color2)

        edit_text_name.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        game_image_view.isEnabled = enable
        //TODO enable/disable platforms spinner
        //TODO enable/disable genres spinner
        //TODO enable/disable formats spinner
        //TODO enable/disable release date spinner
        rating_bar.setIsIndicator(!enable)
        pending_button.isEnabled = enable
        in_progress_button.isEnabled = enable
        finished_button.isEnabled = enable
        edit_text_distributor.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        edit_text_developer.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        //TODO enable/disable pegi spinner
        edit_text_players.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        edit_text_price.setReadOnly(!enable, if (enable) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_NULL, editTextBackgroundColor)
        //TODO enable/disable purchase date spinner
        edit_text_purchase_location.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        radio_button_no.isClickable = enable
        radio_button_yes.isClickable = enable
        edit_text_loaned.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        edit_text_video_url.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        edit_text_observations.setReadOnly(!enable, inputTypeText, editTextBackgroundColor)
        button_add_song.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun setImage() {
    }

    private fun addSong() {
    }

    private fun deleteGame() {
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        text_view_rating.text = rating.toString()
    }
}
