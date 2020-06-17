package es.upsa.mimo.gamercollection.activities.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamercollection.fragments.popups.PopupErrorDialogFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupLoadingDialogFragment
import es.upsa.mimo.gamercollection.models.ErrorResponse

open class BaseActivity : AppCompatActivity() {

    private var loadingFragment: PopupLoadingDialogFragment? = null

    fun manageError(errorResponse: ErrorResponse) {

        hideLoading()
        showPopupDialog(errorResponse.error)
    }

    fun showPopupDialog(message: String) {

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("popupDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupErrorDialogFragment(message)
        dialogFragment.show(ft, "popupDialog")
    }

    fun showLoading() {

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("loadingDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        loadingFragment = PopupLoadingDialogFragment()
        loadingFragment?.let {
            it.isCancelable = false
            it.show(ft, "loadingDialog")
        }
    }

    fun hideLoading() {

        loadingFragment?.dismiss()
        loadingFragment = null
    }
}