package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.RegisterActivity
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.injection.GamerCollectionApplication
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.utils.Constants
import es.upsa.mimo.gamercollection.utils.Environment
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : BaseFragment() {

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
    lateinit var userAPIClient: UserAPIClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = activity?.application
        (application as GamerCollectionApplication).appComponent.inject(this)

        initializeUI()
    }

    //MARK: - Private functions

    private fun initializeUI() {

        val username = sharedPrefHandler.getUserData().username
        val user = if (username.isEmpty()) Environment.getUsername() else username
        edit_text_user.setText(user)
        val password = if (username.isEmpty()) Environment.getPassword() else ""
        edit_text_password.setText(password)

        login_button.setOnClickListener {login()}
        register_button.setOnClickListener {register()}
    }

    private fun login() {

        val username = edit_text_user.text.toString()
        val password = edit_text_password.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            showPopupDialog(resources.getString(R.string.error_registration_empty_data))
            return
        }

        showLoading()
        userAPIClient.login(username, password, { token ->

            val userData = UserData(username, password, false)
            val authData = AuthData(token)
            sharedPrefHandler.run {
                storeUserData(userData)
                storeCredentials(authData)
            }
            syncApp(userData)
        }, {
            manageError(it)
        })
    }

    private fun register() {
        launchActivity(RegisterActivity::class.java)
    }

    private fun syncApp(userData: UserData) {

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

                                userData.isLoggedIn = true
                                sharedPrefHandler.storeUserData(userData)
                                launchActivity(MainActivity::class.java)
                                hideLoading()
                            }, {
                                manageError(it)
                            })
                        }, {
                            manageError(it)
                        })
                    }, {
                        manageError(it)
                    })
                }, {
                    manageError(it)
                })
            }, {
                manageError(it)
            })
        }, {
            manageError(it)
        })
    }
}
