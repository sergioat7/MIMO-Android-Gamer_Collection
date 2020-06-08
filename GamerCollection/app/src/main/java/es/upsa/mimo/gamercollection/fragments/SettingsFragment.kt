package es.upsa.mimo.gamercollection.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.LandingActivity
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)

        initializeUI()
    }

    // MARK: - Private functions

    private fun initializeUI() {

        spinner_languages.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color2))
        spinner_languages.adapter = Constants.getAdapter(requireContext(), resources.getStringArray(R.array.languages).toList())
        spinner_sorting_keys.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color2))
        spinner_sorting_keys.adapter = Constants.getAdapter(requireContext(), resources.getStringArray(R.array.sorting_keys).toList())

        button_save.setOnClickListener { save() }
    }

    private fun save() {

        val landing = Intent(requireContext(), LandingActivity::class.java)
        startActivity(landing)
        activity?.finish()
    }
}