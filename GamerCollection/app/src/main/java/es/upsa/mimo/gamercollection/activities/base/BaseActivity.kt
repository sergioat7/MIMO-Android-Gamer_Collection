package es.upsa.mimo.gamercollection.activities.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamercollection.fragments.popups.PopupErrorDialogFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupLoadingDialogFragment
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.utils.Constants

open class BaseActivity : AppCompatActivity() {

    private var loadingFragment: PopupLoadingDialogFragment? = null

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

    fun showPopupDialog(message: String) {

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(Constants.POPUP_DIALOG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupErrorDialogFragment(message)
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
}