package es.upsa.mimo.gamercollection.fragments.base

import android.app.AlertDialog
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.fragments.popups.PopupErrorDialogFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupLoadingDialogFragment
import es.upsa.mimo.gamercollection.fragments.popups.PopupSyncAppDialogFragment
import es.upsa.mimo.gamercollection.models.ErrorResponse
import java.io.Serializable

open class BaseFragment : Fragment() {

    private var loadingFragment: PopupLoadingDialogFragment? = null

    fun manageError(errorResponse: ErrorResponse) {

        hideLoading()
        showPopupDialog(errorResponse.error)
    }

    fun <T> launchActivity(activity: Class<T>) {

        val intent = Intent(context, activity).apply {}
        startActivity(intent)
    }

    fun <T> launchActivityWithExtras(activity: Class<T>, params: Map<String, Serializable>) {

        val intent = Intent(context, activity).apply {}
        for (param in params) {
            intent.putExtra(param.key, param.value)
        }
        startActivity(intent)
    }

    fun showLoading() {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("loadingDialog")
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

    fun showPopupDialog(message: String) {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("popupDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupErrorDialogFragment(message)
        dialogFragment.show(ft, "popupDialog")
    }

    fun showPopupConfirmationDialog(message: String, acceptHandler: () -> Unit) {

        AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.ACCEPT)) { dialog, _ ->
                acceptHandler()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.CANCEL)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun openSyncPopup() {

        showPopupConfirmationDialog(resources.getString(R.string.SYNC_CONFIRMATION)) {
            showSyncPopup()
        }
    }

    //MARK - Private functions

    private fun showSyncPopup() {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("syncDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupSyncAppDialogFragment()
        dialogFragment.isCancelable = false
        dialogFragment.show(ft, "syncDialog")
    }
}
