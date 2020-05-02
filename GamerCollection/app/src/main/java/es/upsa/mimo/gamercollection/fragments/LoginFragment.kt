package es.upsa.mimo.gamercollection.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.MainActivity
import es.upsa.mimo.gamercollection.activities.RegisterActivity
import es.upsa.mimo.gamercollection.activities.base.BaseFragment
import es.upsa.mimo.gamercollection.models.*
import es.upsa.mimo.gamercollection.network.apiClient.*
import es.upsa.mimo.gamercollection.persistence.repositories.*
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var formatRepository: FormatRepository
    private lateinit var genreRepository: GenreRepository
    private lateinit var platformRepository: PlatformRepository
    private lateinit var stateRepository: StateRepository
    private lateinit var gameRepository: GameRepository
    private lateinit var sagaRepository: SagaRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        formatRepository = FormatRepository(requireContext())
        genreRepository = GenreRepository(requireContext())
        platformRepository = PlatformRepository(requireContext())
        stateRepository = StateRepository(requireContext())
        gameRepository = GameRepository(requireContext())
        sagaRepository = SagaRepository(requireContext())

        checkIsNewInstallation()
        showMainView()
    }

    //MARK: - Private functions

    //TODO remove all about isNewInstallation, it's unnecessary
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

        val intent = Intent(context, RegisterActivity::class.java).apply {}
        startActivity(intent)
    }

    private fun syncApp(userData: UserData) {

        FormatAPIClient.getFormats(resources, { formats ->
            GenreAPIClient.getGenres(resources, { genres ->
                PlatformAPIClient.getPlatforms(resources, { platforms ->
                    StateAPIClient.getStates(resources, { states ->
                        GameAPIClient.getGames(sharedPrefHandler, resources, { games ->
                            SagaAPIClient.getSagas(sharedPrefHandler, resources, { sagas ->

                                manageFormats(formats)
                                manageGenres(genres)
                                managePlatforms(platforms)
                                manageStates(states)
                                manageGames(games)
                                manageSagas(sagas)

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

    private fun manageFormats(formats: List<FormatResponse>) {

        for (format in formats) {
            formatRepository.insertFormat(format)
        }
        formatRepository.removeDisableContent(formats)
    }

    private fun manageGenres(genres: List<GenreResponse>) {

        for (genre in genres) {
            genreRepository.insertGenre(genre)
        }
        genreRepository.removeDisableContent(genres)
    }

    private fun managePlatforms(platforms: List<PlatformResponse>) {

        for (platform in platforms) {
            platformRepository.insertPlatform(platform)
        }
        platformRepository.removeDisableContent(platforms)
    }

    private fun manageStates(states: List<StateResponse>) {

        for (state in states) {
            stateRepository.insertState(state)
        }
        stateRepository.removeDisableContent(states)
    }

    private fun manageGames(games: List<GameResponse>) {

        for (game in games) {
            gameRepository.insertGame(game)
        }
        gameRepository.removeDisableContent(games)
    }

    private fun manageSagas(sagas: List<SagaResponse>) {

        for (saga in sagas) {
            sagaRepository.insertSaga(saga)
        }
        sagaRepository.removeDisableContent(sagas)
    }
}
