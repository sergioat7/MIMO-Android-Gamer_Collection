package es.upsa.mimo.gamercollection.fragments.popups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.databinding.FragmentPopupErrorDialogBinding

class PopupErrorDialogFragment(
    private val message: String,
    private val goBack: MutableLiveData<Boolean>? = null
) : DialogFragment() {

    //region Private properties
    private lateinit var binding: FragmentPopupErrorDialogBinding
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_GamerCollection_DialogTransparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_popup_error_dialog,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.text = message
        binding.fragment = this
    }
    //endregion

    //region Public methods
    fun close() {

        dismiss()
        goBack?.let {
            it.value = true
        }
    }
    //endregion
}
