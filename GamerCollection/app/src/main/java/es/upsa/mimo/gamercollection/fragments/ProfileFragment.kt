package es.upsa.mimo.gamercollection.fragments

import android.os.Bundle
import android.text.InputType
import android.view.*
import es.upsa.mimo.gamercollection.R
import es.upsa.mimo.gamercollection.activities.LoginActivity
import es.upsa.mimo.gamercollection.extensions.setReadOnly
import es.upsa.mimo.gamercollection.fragments.base.BaseFragment
import es.upsa.mimo.gamercollection.models.AuthData
import es.upsa.mimo.gamercollection.network.apiClient.UserAPIClient
import es.upsa.mimo.gamercollection.persistence.repositories.GameRepository
import es.upsa.mimo.gamercollection.persistence.repositories.SagaRepository
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHandler
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {

    private lateinit var sharedPrefHandler: SharedPreferencesHandler
    private lateinit var userAPIClient: UserAPIClient
    private lateinit var gameRepository: GameRepository
    private lateinit var sagaRepository: SagaRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefHandler = SharedPreferencesHandler(context)
        userAPIClient = UserAPIClient(resources, sharedPrefHandler)
        gameRepository = GameRepository(requireContext())
        sagaRepository = SagaRepository(requireContext())

        initializeUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.profile_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_synchronize -> {
                openSyncPopup()
                return true
            }
            R.id.action_logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //MARK: - Private functions

    private fun initializeUI() {

        val userData = sharedPrefHandler.getUserData()
        edit_text_user.setText(userData.username)
        edit_text_password.setText(userData.password)

        edit_text_user.setReadOnly(true, InputType.TYPE_NULL, 0)

        button_change_password.setOnClickListener { updatePassword() }
        button_delete_user.setOnClickListener { deleteUser() }
    }

    private fun logout() {

        showPopupConfirmationDialog(resources.getString(R.string.PROFILE_LOGOUT_CONFIRMATION)) {

            showLoading()
            userAPIClient.logout({

                sharedPrefHandler.removePassword()
                removeData()
            }, {

                sharedPrefHandler.removePassword()
                removeData()
            })
        }
    }


    private fun updatePassword() {

        val currentPassword = sharedPrefHandler.getUserData().password
        val newPassword = edit_text_password.text.toString()

        if (currentPassword == newPassword) return

        showLoading()
        userAPIClient.updatePassword(newPassword, {
            sharedPrefHandler.storePassword(newPassword)
            val userData = sharedPrefHandler.getUserData()
            userAPIClient.login(userData.username, userData.password, {

                val authData = AuthData(it)
                sharedPrefHandler.storeCredentials(authData)
                hideLoading()
            }, {
                manageError(it)
            })
        }, {
            manageError(it)
        })
    }

    private fun deleteUser() {

        showPopupConfirmationDialog(resources.getString(R.string.PROFILE_DELETE_CONFIRMATION)) {

            showLoading()
            userAPIClient.deleteUser({

                sharedPrefHandler.removeUserData()
                removeData()
            }, {
                manageError(it)
            })
        }
    }

    private fun removeData() {

        sharedPrefHandler.removeCredentials()
        val games = gameRepository.getGames()
        for (game in games) {
            gameRepository.deleteGame(game)
        }
        val sagas = sagaRepository.getSagas()
        for (saga in sagas) {
            sagaRepository.deleteSaga(saga)
        }

        hideLoading()
        launchActivity(LoginActivity::class.java)
    }
}
