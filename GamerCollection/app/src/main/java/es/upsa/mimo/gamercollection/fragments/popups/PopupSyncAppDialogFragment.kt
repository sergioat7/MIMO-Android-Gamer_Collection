package es.upsa.mimo.gamercollection.fragments.popups

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler

class PopupSyncAppDialogFragment : DialogFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var formatAPIClient: FormatAPIClient
    private lateinit var genreAPIClient: GenreAPIClient
    private lateinit var platformAPIClient: PlatformAPIClient
    private lateinit var stateAPIClient: StateAPIClient
    private lateinit var gameAPIClient: GameAPIClient
    private lateinit var sagaAPIClient: SagaAPIClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popup_sync_app_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        formatAPIClient = FormatAPIClient(resources, sharedPrefHandler)
        genreAPIClient = GenreAPIClient(resources, sharedPrefHandler)
        platformAPIClient = PlatformAPIClient(resources, sharedPrefHandler)
        stateAPIClient = StateAPIClient(resources, sharedPrefHandler)
        gameAPIClient = GameAPIClient(resources, sharedPrefHandler)
        sagaAPIClient = SagaAPIClient(resources, sharedPrefHandler)

        syncApp()
    }

    //MARK: - Private functions

    private fun syncApp() {

        formatAPIClient.getFormats({ formats ->
            genreAPIClient.getGenres({ genres ->
                platformAPIClient.getPlatforms({ platforms ->
                    stateAPIClient.getStates({ states ->
                        gameAPIClient.getGames({ games ->
                            sagaAPIClient.getSagas({ sagas ->

                                Constants.manageFormats(requireContext(), formats)
                                Constants.manageGenres(requireContext(), genres)
                                Constants.managePlatforms(requireContext(), platforms)
                                Constants.manageStates(requireContext(), states)
                                Constants.manageGames(requireContext(), games)
                                Constants.manageSagas(requireContext(), sagas)

                                goToMainView()
                                dismiss()
                            }, {
                                dismiss()
                                showPopupDialog(it.error)
                            })
                        }, {
                            dismiss()
                            showPopupDialog(it.error)
                        })
                    }, {
                        dismiss()
                        showPopupDialog(it.error)
                    })
                }, {
                    dismiss()
                    showPopupDialog(it.error)
                })
            }, {
                dismiss()
                showPopupDialog(it.error)
            })
        }, {
            dismiss()
            showPopupDialog(it.error)
        })
    }

    private fun goToMainView() {

        val intent = Intent(context, MainActivity::class.java).apply {}
        startActivity(intent)
    }

    private fun showPopupDialog(message: String) {

        val ft: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        val prev = activity?.supportFragmentManager?.findFragmentByTag("popupDialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment = PopupErrorDialogFragment(message)
        dialogFragment.show(ft, "popupDialog")
    }
}
