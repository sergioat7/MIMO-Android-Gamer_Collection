package es.upsa.mimo.gamercollection.activities.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamercollection.fragments.popups.PopupErrorDialogFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupLoadingDialogFragment


open class BaseActivity : AppCompatActivity() {

    private var loadingFragment: PopupLoadingDialogFragment? = null

    fun showPopupDialog(message: String) {

        val ft: FragmentTransaction = supportFragmentManager?.beginTransaction()
        val prev = supportFragmentManager?.findFragmentByTag("popupDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupErrorDialogFragment(message)
        dialogFragment.show(ft, "popupDialog")
    }

    fun showLoading() {

        val ft: FragmentTransaction = supportFragmentManager?.beginTransaction() ?: return
        val prev = supportFragmentManager?.findFragmentByTag("loadingDialog")
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