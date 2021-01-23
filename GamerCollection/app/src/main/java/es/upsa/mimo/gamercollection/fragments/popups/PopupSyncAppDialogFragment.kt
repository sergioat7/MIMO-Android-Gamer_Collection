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
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import javax.inject.Inject

class PopupSyncAppDialogFragment : DialogFragment() {

    @Inject
    lateinit var sharedPrefHandler: SharedPreferencesHandler
    @Inject
    lateinit var formatAPIClient: FormatAPIClient
    @Inject
    lateinit var gameAPIClient: GameAPIClient
    @Inject
    lateinit var genreAPIClient: GenreAPIClient
    @Inject
    lateinit var platformAPIClient: PlatformAPIClient
    @Inject
    lateinit var sagaAPIClient: SagaAPIClient
    @Inject
    lateinit var stateAPIClient: StateAPIClient
    @Inject
    lateinit var formatRepository: FormatRepository
    @Inject
    lateinit var gameRepository: GameRepository
    @Inject
    lateinit var genreRepository: GenreRepository
    @Inject
    lateinit var platformRepository: PlatformRepository
    @Inject
    lateinit var sagaRepository: SagaRepository
    @Inject
    lateinit var stateRepository: StateRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popup_sync_app_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = activity?.application
        (application as GamerCollectionApplication).appComponent.inject(this)

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

                                formatRepository.manageFormats(formats)
                                genreRepository.manageGenres(genres)
                                platformRepository.managePlatforms(platforms)
                                stateRepository.manageStates(states)
                                gameRepository.manageGames(games)
                                sagaRepository.manageSagas(sagas)

                                goToMainView()
                                dismiss()
                            }, {
                                dismiss()
                                manageError(it)
                            })
                        }, {
                            dismiss()
                            manageError(it)
                        })
                    }, {
                        dismiss()
                        manageError(it)
                    })
                }, {
                    dismiss()
                    manageError(it)
                })
            }, {
                dismiss()
                manageError(it)
            })
        }, {
            dismiss()
            manageError(it)
        })
    }

    private fun goToMainView() {

        val intent = Intent(context, MainActivity::class.java).apply {}
        startActivity(intent)
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
