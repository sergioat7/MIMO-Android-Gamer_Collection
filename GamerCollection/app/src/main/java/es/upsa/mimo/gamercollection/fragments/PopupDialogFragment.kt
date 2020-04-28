package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import es.upsa.mimo.gamercollection.R
import kotlinx.android.synthetic.main.fragment_popup_dialog.*

class PopupDialogFragment(private val message: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popup_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        error_text_view.text = message
        accept_button.setOnClickListener { dismiss() }
    }

}
