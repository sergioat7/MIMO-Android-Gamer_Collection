package es.upsa.mimo.gamercollection.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.base.BaseFragment
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.models.UserData
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.persistence.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        checkIsNewInstallation()
        showMainView()
    }

    //MARK: - Private functions

    private fun checkIsNewInstallation() {

        if (sharedPrefHandler.isNewInstallation()) {
            sharedPrefHandler.removeUserData()
            sharedPrefHandler.removeCredentials()
            sharedPrefHandler.setIsNewInstallation()
        }
    }

    private fun showMainView() {

        if (sharedPrefHandler.isLoggedIn()) {
            goToMainView()
        } else {
            initializeUI()
        }
    }

    private fun initializeUI() {

        val username = sharedPrefHandler.getUserData().username
        editTextEmail.setText(username)

        login_button.setOnClickListener {login()}
        register_button.setOnClickListener {register()}
    }

    private fun login() {

        val username = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        showLoading(view)
        LoginAPIClient.login(username, password, resources, { token ->

            val userData = UserData(username, password, false)
            val authData = AuthData(token)
            sharedPrefHandler.run {
                storeUserData(userData)
                storeCredentials(authData)
            }
            syncApp(userData)
        }, { errorResponse ->
            manageError(errorResponse)
        })
    }

    private fun register() {
        //TODO go to register view
    }

    private fun syncApp(userData: UserData) {

        FormatAPIClient.getFormats(resources, { formats ->
            GenreAPIClient.getGenres(resources, { genres ->
                PlatformAPIClient.getPlatforms(resources, { platforms ->
                    StateAPIClient.getStates(resources, { states ->
                        SagaAPIClient.getSagas(sharedPrefHandler, resources, { sagas ->
                            GameAPIClient.getGames(sharedPrefHandler, resources, { games ->

                                //TODO do it in background
                                val formatRepository = FormatRepository(requireContext())
                                for (format in formats) {
                                    formatRepository.insertFormat(format)
                                }
                                //TODO do it in background
                                val genreRepository = GenreRepository(requireContext())
                                for (genre in genres) {
                                    genreRepository.insertGenre(genre)
                                }
                                //TODO do it in background
                                val platformRepository = PlatformRepository(requireContext())
                                for (platform in platforms) {
                                    platformRepository.insertPlatform(platform)
                                }
                                //TODO do it in background
                                val stateRepository = StateRepository(requireContext())
                                for (state in states) {
                                    stateRepository.insertState(state)
                                }
                                //TODO store sagas
                                //TODO do it in background
                                val gameRepository = GameRepository(requireContext())
                                for (game in games) {
                                    gameRepository.insertGame(game)
                                }
                                userData.isLoggedIn = true
                                sharedPrefHandler.storeUserData(userData)
                                goToMainView()
                                hideLoading()
                            }, { errorResponse ->
                                manageError(errorResponse)
                            })
                        }, { errorResponse ->
                            manageError(errorResponse)
                        })
                    }, { errorResponse ->
                        manageError(errorResponse)
                    })
                }, { errorResponse ->
                    manageError(errorResponse)
                })
            }, { errorResponse ->
                manageError(errorResponse)
            })
        }, { errorResponse ->
            manageError(errorResponse)
        })
    }

    private fun goToMainView() {

        val intent = Intent(context, MainActivity::class.java).apply {}
        startActivity(intent)
    }
}
