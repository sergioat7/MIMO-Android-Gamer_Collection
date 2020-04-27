package es.upsa.mimo.gamercollection.activities.base

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.fragments.PopupDialogFragment


open class BaseFragment : Fragment() {

    private lateinit var progressBar: ProgressBar

    fun showLoading(view: View?) {

        if (view == null) return
        val context = view.context
        progressBar = ProgressBar(context)
        progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, R.color.color3), android.graphics.PorterDuff.Mode.MULTIPLY)
        val parentLayout = LinearLayout(context)
        parentLayout.layoutParams = view.layoutParams
        parentLayout.gravity = Gravity.CENTER
        if (view.parent != null) (view.parent as ViewGroup).addView(parentLayout)
        progressBar.isIndeterminate = true
        progressBar.tag = view
        parentLayout.addView(progressBar)
        view.visibility = View.GONE
    }

    fun hideLoading() {

        if (progressBar == null) return
        val view = progressBar.tag as View
        (view.parent as ViewGroup).removeView(progressBar.parent as View)
        view.visibility = View.VISIBLE
    }

    fun showPopupDialog(message: String) {

        val ft: FragmentTransaction = fragmentManager?.beginTransaction() ?: return
        val prev = fragmentManager?.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment: DialogFragment = PopupDialogFragment(message)
        dialogFragment.show(ft, "dialog")
    }
}
