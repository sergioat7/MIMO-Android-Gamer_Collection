package es.upsa.mimo.gamercollection.fragments.popups

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.base.BindingDialogFragment
import es.upsa.mimo.gamercollection.databinding.FragmentPopupSyncAppDialogBinding
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.viewmodelfactories.PopupSyncAppViewModelFactory
import es.upsa.mimo.gamercollection.viewmodels.PopupSyncAppViewModel

class PopupSyncAppDialogFragment : BindingDialogFragment<FragmentPopupSyncAppDialogBinding>() {

    //region Protected properties
    override val transparentStyle = true
    //endregion

    //region Private properties
    private lateinit var viewModel: PopupSyncAppViewModel
    //endregion

    //region Lifecycle methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
    }
    //endregion

    //region Private methods
    private fun initializeUI() {

        val application = activity?.application
        viewModel = ViewModelProvider(
            this,
            PopupSyncAppViewModelFactory(application)
        )[PopupSyncAppViewModel::class.java]
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
