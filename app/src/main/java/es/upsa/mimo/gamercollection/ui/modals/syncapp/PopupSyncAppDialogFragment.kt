package es.upsa.mimo.gamercollection.ui.modals.syncapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.ui.MainActivity
import es.upsa.mimo.gamercollection.ui.base.BindingDialogFragment
import es.upsa.mimo.gamercollection.databinding.DialogFragmentPopupSyncAppBinding
import es.upsa.mimo.gamercollection.models.ErrorResponse

@AndroidEntryPoint
class PopupSyncAppDialogFragment : BindingDialogFragment<DialogFragmentPopupSyncAppBinding>() {

    //region Protected properties
    override val transparentStyle = true
    //endregion

    //region Private properties
    private val viewModel: PopupSyncAppViewModel by viewModels()
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        setupBindings()

        viewModel.loadContent()
    }

    private fun setupBindings() {

        viewModel.popupSyncAppError.observe(viewLifecycleOwner) { error ->

            dismiss()
            if (error == null) {

                val intent = Intent(context, MainActivity::class.java).apply {}
                startActivity(intent)
            } else {
                manageError(error)
            }
        }
    }

    private fun manageError(errorResponse: ErrorResponse) {

        val error = StringBuilder()
        if (errorResponse.error.isNotEmpty()) {
            error.append(errorResponse.error)
        } else {
            error.append(resources.getString(errorResponse.errorKey))
        }
        showPopupDialog(error.toString())
    }

    private fun showPopupDialog(message: String) {

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }
    //endregion
}
