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

        val languageIds = resources.getStringArray(R.array.languages_ids)
        languageIds.firstOrNull { it == sharedPrefHandler.getLanguage() }?.let {
            spinner_languages.setSelection(languageIds.indexOf(it))
        } ?: run {
            spinner_languages.setSelection(0)
        }

        val sortingKeyIds = resources.getStringArray(R.array.sorting_keys_ids)
        sortingKeyIds.firstOrNull { it == sharedPrefHandler.getSortingKey() }?.let {
            spinner_sorting_keys.setSelection(sortingKeyIds.indexOf(it))
        } ?: run {
            spinner_sorting_keys.setSelection(0)
        }

        switch_swipe_refresh.isChecked = sharedPrefHandler.getSwipeRefresh()

        button_save.setOnClickListener { save() }
    }

    private fun save() {

        val languages = resources.getStringArray(R.array.languages)
        languages.firstOrNull { it == spinner_languages.selectedItem.toString() }?.let {
            val languageId = resources.getStringArray(R.array.languages_ids)[languages.indexOf(it)]
            sharedPrefHandler.setLanguage(languageId)
        } ?: run {
            sharedPrefHandler.setLanguage(languages[0])
        }

        val sortingKeys = resources.getStringArray(R.array.sorting_keys)
        sortingKeys.firstOrNull { it == spinner_sorting_keys.selectedItem.toString() }?.let {
            val sortingKeyId = resources.getStringArray(R.array.sorting_keys_ids)[sortingKeys.indexOf(it)]
            sharedPrefHandler.setSortingKey(sortingKeyId)
        } ?: run {
            sharedPrefHandler.setSortingKey(sortingKeys[0])
        }

        sharedPrefHandler.setSwipeRefresh(switch_swipe_refresh.isChecked)

        val landing = Intent(requireContext(), LandingActivity::class.java)
        startActivity(landing)
        activity?.finish()
    }
}