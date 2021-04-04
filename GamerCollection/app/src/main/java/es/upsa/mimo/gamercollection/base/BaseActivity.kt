package es.upsa.mimo.gamercollection.base

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.fragments.popups.PopupErrorDialogFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupLoadingDialogFragment
import es.upsa.mimo.gamercollection.models.responses.ErrorResponse
import es.upsa.mimo.gamercollection.utils.Constants

open class BaseActivity : AppCompatActivity() {

    //region Private properties
    private var loadingFragment: PopupLoadingDialogFragment? = null
    //endregion

    //region Public methods
    fun manageError(errorResponse: ErrorResponse) {

        hideLoading()
        val error = StringBuilder()
        if (errorResponse.error.isNotEmpty()) {
            error.append(errorResponse.error)
        } else {
            error.append(resources.getString(errorResponse.errorKey))
        }
        showPopupDialog(error.toString())
    }

    fun showPopupDialog(message: String, goBack: MutableLiveData<Boolean>? = null) {

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(Constants.POPUP_DIALOG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupErrorDialogFragment(message, goBack)
        dialogFragment.show(ft, Constants.POPUP_DIALOG)
    }

    fun showLoading() {

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(Constants.LOADING_DIALOG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        loadingFragment = PopupLoadingDialogFragment()
        loadingFragment?.let {
            it.isCancelable = false
            it.show(ft, Constants.LOADING_DIALOG)
        }
    }

    fun hideLoading() {

        loadingFragment?.dismiss()
        loadingFragment = null
    }

    fun showPopupConfirmationDialog(message: String, acceptHandler: () -> Unit) {

        AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                acceptHandler()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    //endregion
}